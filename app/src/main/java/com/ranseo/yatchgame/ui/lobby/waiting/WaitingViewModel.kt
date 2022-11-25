package com.ranseo.yatchgame.ui.lobby.waiting

import android.app.Application
import androidx.lifecycle.*
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.data.repo.WaitingRepositery
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.util.DateTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.lang.Exception


class WaitingViewModel @AssistedInject constructor(
    private val waitingRepositery: WaitingRepositery,
    @Assisted private val roomId: String,
    application: Application
) : AndroidViewModel(application) {
    private val TAG = "WaitingViewModel"
    private var updateFlag: Boolean = true

    private lateinit var player: Player

    private val _waitingRoom = MutableLiveData<WaitingRoom>()
    val waitingRoom: LiveData<WaitingRoom>
        get() = _waitingRoom

    val waiting = Transformations.map(waitingRoom) {
        it.guest != null
    }

    private val _gameInfo = MutableLiveData<Event<Boolean>>()
    val gameInfo : LiveData<Event<Boolean>>
        get() = _gameInfo

    init {
        viewModelScope.launch {
            launch {
                player = waitingRepositery.refreshHostPlayer()
            }.join()

            if (roomId == getApplication<Application?>().getString(R.string.make_wait_room)) {//roomId가 R.string.make_wait_room과 같다면 Host가 방을 생성한다.
                launch {
                    val new = WaitingRoom(
                        player.playerId,
                        mutableMapOf("host" to player),
                        null
                    )

                    writeWaitingRoom(new)
                }.join()

                refreshWaitingRoom(player.playerId)

            } else {
                refreshWaitingRoom(roomId)
            }
        }
    }

    /**
     * WaitingRoom 정보 읽어오기
     * */
    private suspend fun refreshWaitingRoom(roomId: String) {

        waitingRepositery.getWaitingRoom(roomId) { waitingRoom ->
            viewModelScope.launch {
                _waitingRoom.postValue(waitingRoom)
            }
        }

    }

    /**
     * Host인 경우 WaitingRoom 객체 Write
     * */
    private suspend fun writeWaitingRoom(waitingRoom: WaitingRoom) {
        waitingRepositery.writeWaitingRoom(waitingRoom)
    }

    /**
     * Guest인 경우 WaitingRoom Update
     * */
    fun updateWaitingRoom(waitingRoom: WaitingRoom) {
        viewModelScope.launch {
            log(TAG, "before updateWaitingRoom : ${waitingRoom}", LogTag.I)
            if (!updateFlag) return@launch
            try {
                updateFlag = false
                log(TAG, "try updateWaitingRoom : ${waitingRoom}", LogTag.I)
                val new = WaitingRoom(
                    waitingRoom,
                    mutableMapOf("guest" to player)
                )
                log(TAG, "try updateWaitingRoom : ${new}", LogTag.I)
                waitingRepositery.updateWaitingRoom(new)

                log(TAG, "updateWaitingRoom : ${new}", LogTag.I)
            } catch (error: Exception) {
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
            waitingRepositery.removeWaitingRoomValue(roomId)
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
            waitingRepositery.removeWaitingRoomValueEventListener(roomId)
        }
    }


    /**
     * Host와 Guest플레이어가 모두 대기실에 입장한 뒤,
     * 게임을 실행하기 위해 GameInfo 객체를 생성하여 RoomDatabase와 Firebase Database에 Insert or Write.
     *
     * 만약 Firebase Database에 GameInfoFirebaseModel을 Write하는데 성공했다면
     * callback : (gameInfo: GameInfoFirebaseModel) -> Unit 을 이용하여 viewModel의 '_gaemInfo' value에 할당
     * */
    fun writeGameInfo() {
        viewModelScope.launch {
            val wr = waitingRoom.value
            if (wr?.getGuestPlayer() == null || wr.getHostPlayer() == null) return@launch

            val newGameInfo = GameInfo(wr,DateTime.getNowDate(), listOf(Board(), Board()))
            waitingRepositery.writeGameInfoAtFirst(newGameInfo) { flag ->
                viewModelScope.launch {
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