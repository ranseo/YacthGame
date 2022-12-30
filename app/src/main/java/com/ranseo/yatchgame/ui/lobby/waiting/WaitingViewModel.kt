package com.ranseo.yatchgame.ui.lobby.waiting

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.data.repo.WaitingRoomRepository
import com.ranseo.yatchgame.domain.usecase.GetPlayerUseCase
import com.ranseo.yatchgame.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.lang.Exception


class WaitingViewModel @AssistedInject constructor(
    private val waitingRoomRepository: WaitingRoomRepository,
    private val getPlayerUseCase: GetPlayerUseCase,
    @Assisted private val waitingRoomId: String,
    application: Application
) : AndroidViewModel(application) {
    private val TAG = "WaitingViewModel"
    private var updateFlag: Boolean = true

    val player: LiveData<Player> = getPlayerUseCase()

    private val _waitingRoom = MutableLiveData<WaitingRoom>()
    val waitingRoom: LiveData<WaitingRoom>
        get() = _waitingRoom


    @RequiresApi(Build.VERSION_CODES.N)
    val waiting = Transformations.map(waitingRoom) {
        val guest = it.guest["guest"]!!
        guest.playerId != Player.getEmptyPlayer().playerId
    }

    private val _gameInfo = MutableLiveData<Event<Boolean>>()
    val gameInfo: LiveData<Event<Boolean>>
        get() = _gameInfo


    fun makeWaitingRoom(player: Player) {
        viewModelScope.launch {
            if (waitingRoomId.substringBefore(getApplication<Application?>().getString(R.string.border_string_for_parsing))
                == getApplication<Application?>().getString(R.string.make_wait_room)
            ) {
                //roomId가 R.string.make_wait_room과 같다면 Host가 방을 생성한다.
                val waitingRoomId =
                    waitingRoomId.substringAfter(getApplication<Application?>().getString(R.string.border_string_for_parsing))
                launch {

                    val new = WaitingRoom(
                        waitingRoomId,
                        mutableMapOf("host" to player),
                        mutableMapOf("guest" to Player.getEmptyPlayer()),
                    )

                    writeWaitingRoom(new)
                }.join()

                refreshWaitingRoom(waitingRoomId)
            } else {
                launch {
                    refreshWaitingRoom(waitingRoomId)
                }
            }
        }
    }

    /**
     * WaitingRoom 정보 읽어오기
     * */
    private suspend fun refreshWaitingRoom(roomId: String) {

        waitingRoomRepository.getWaitingRoom(roomId) { waitingRoom ->
            viewModelScope.launch {
                log(
                    TAG,
                    "refreshWaitingRoom : ${waitingRoom} , ${waitingRoom.roomId}, ${waitingRoom.host}, ${waitingRoom.guest}, ${waitingRoom.guest["guest"]}",
                    LogTag.I
                )
                _waitingRoom.postValue(waitingRoom)
            }
        }

    }

    /**
     * Host인 경우 WaitingRoom 객체 Write
     * */
    private suspend fun writeWaitingRoom(waitingRoom: WaitingRoom) {
        waitingRoomRepository.writeWaitingRoom(waitingRoom)
    }

    /**
     * Guest인 경우 WaitingRoom Update
     * */
    fun updateWaitingRoom(waitingRoom: WaitingRoom) {
        viewModelScope.launch {
            if (!updateFlag) return@launch
            try {
                updateFlag = false

                val new = WaitingRoom(
                    waitingRoom,
                    mutableMapOf("guest" to player.value!!)
                )

                waitingRoomRepository.updateWaitingRoom(new)
                log(TAG, "updateWaitingRoom : ${new}", LogTag.I)
            } catch (error: Exception) {
                updateFlag = true
                log(TAG, "updateWaitingRoom Error : ${error.message}", LogTag.D)
            }catch (error:Exception) {
                updateFlag = true
                log(TAG, "updateWaitingRoom Error : ${error.message}", LogTag.D)
            }
        }
    }

    /**
     * 호스트와 게스트 플레이어가 모두 대기실에 접속하여, 게임룸으로 이동할 때,
     * 해당 WaitingRoom을 Firebase Database에 삭제.
     * */
    fun removeWaitingRoomValue() {
        viewModelScope.launch {
            val roomId = waitingRoom.value?.roomId ?: return@launch
            waitingRoomRepository.removeWaitingRoomValue(roomId)
        }
    }

    /**
     * ViewModel 제거될 때
     *
     * Database에 연결된 WaitingRoom ValueEventListener 제거
     * */
    fun removeListener() {
        viewModelScope.launch {
            val roomId: String = waitingRoom.value?.roomId ?: return@launch
            waitingRoomRepository.removeWaitingRoomValueEventListener(roomId)
        }
    }


    /**
     * Host와 Guest플레이어가 모두 대기실에 입장한 뒤,
     * 게임을 실행하기 위해 GameInfo 객체를 생성하여 RoomDatabase와 Firebase Database에 Insert or Write.
     *
     * 만약 Firebase Database에 GameInfoFirebaseModel을 Write하는데 성공했다면
     * callback : (gameInfo: GameInfoFirebaseModel) -> Unit 을 이용하여 viewModel의 '_gaemInfo' value에 할당
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun writeGameInfo() {
        viewModelScope.launch {
            val wr = waitingRoom.value
            if (wr?.guest?.getOrDefault("guest", null) == null || wr.host.getOrDefault(
                    "host",
                    null
                ) == null
            ) return@launch
            waitingRoomRepository.writeGameInfoAtFirst(wr) { flag, gameInfo ->
                viewModelScope.launch {
                    if (flag) waitingRoomRepository.insertGameInfoAtFirst(gameInfo)
                    _gameInfo.postValue(Event(flag))
                }
            }
        }
    }


    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(roomId: String): WaitingViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            roomId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return assistedFactory.create(roomId) as T
            }
        }
    }

}