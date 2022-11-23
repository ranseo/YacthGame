package com.ranseo.yatchgame.ui.lobby

import android.app.Application
import androidx.lifecycle.*
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.model.WaitingRoom
import com.ranseo.yatchgame.data.repo.WaitingRepositery
import com.ranseo.yatchgame.log
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


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

    init {
        log(TAG, "init()", LogTag.I)
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

                launch {
                    refreshWaitingRoom(player.playerId)
                }.join()

            } else { //그렇지 않다면, Guest로서 방에 접속해 WaitingRoom 정보를 업데이트한다.
                launch {
                    refreshWaitingRoom(roomId)
                }.join()

                launch {

                }
            }
        }
    }

    /**
     * 사용자의 Player객체 정보를 set
     * */
    private suspend fun refreshHostPlayer() {
        viewModelScope.launch {

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
            if (!updateFlag) return@launch
            try {
                val new = WaitingRoom(
                    waitingRoom,
                    mutableMapOf("guest" to player)
                )

                waitingRepositery.updateWaitingRoom(new)
                updateFlag = false
                log(TAG, "updateWaitingRoom : ${waitingRoom}", LogTag.I)
            } catch (error: Exception) {
                log(TAG, "updateWaitingRoom Error : ${error.message}", LogTag.D)
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