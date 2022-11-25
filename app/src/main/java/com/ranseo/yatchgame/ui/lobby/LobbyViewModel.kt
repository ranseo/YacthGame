package com.ranseo.yatchgame.ui.lobby

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranseo.yatchgame.Event
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.LobbyRoom
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.repo.LobbyRepositery
import com.ranseo.yatchgame.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobbyViewModel @Inject constructor(private val lobbyRepositery: LobbyRepositery) :
    ViewModel() {
    private val TAG = "LobbyViewModel"
    private lateinit var host : Player

    private val _lobbyRooms = MutableLiveData<List<LobbyRoom>>()
    val lobbyRooms: LiveData<List<LobbyRoom>>
        get() = _lobbyRooms

    private val _makingRoom= MutableLiveData<Event<Any?>>()
    val makingRoom : LiveData<Event<Any?>>
        get() = _makingRoom

    private val _makeWaitRoom = MutableLiveData<Event<Any?>>()
    val makeWaitRoom : LiveData<Event<Any?>>
        get() = _makeWaitRoom

    private val _accessWaitRoom = MutableLiveData<Event<LobbyRoom>>()
    val accessWaitRoom : LiveData<Event<LobbyRoom>>
        get() = _accessWaitRoom

    init {
        refreshHostPlayer()
        refreshLobbyRooms()
    }

    /**
     * 사용자(=Host)의 Player객체 정보를 set
     * */
    private fun refreshHostPlayer() {
        viewModelScope.launch {
            host = lobbyRepositery.refreshHostPlayer()
        }
    }

    /**
     * List<LobbyRoom>을 Firebase Database로 부터 읽어와 callback을 통해
     *
     * _lobbyRooms의 value에 할당
     * */
    private fun refreshLobbyRooms() {
        viewModelScope.launch {
            lobbyRepositery.getLobbyRooms { list ->
                _lobbyRooms.postValue(list)
            }
        }
    }

    /**
     * Firebase Reference에 등록된 ValueEventListener를 Fragment가 onDestroy() 호출할 때 모두 제거.
     * */
    fun removeEventListener() {
        viewModelScope.launch {
            lobbyRepositery.removeEventListener()
        }
    }

    /**
     * Firebase Database에 생성된 LobbyRoom 객체를  Write
     * */
    private fun writeLobbyRoom(lobbyRoom: LobbyRoom) {
        viewModelScope.launch {
            launch {
                lobbyRepositery.writeLobbyRoom(lobbyRoom)
            }.join()

            launch {
                log(TAG,"writeLobbyRoom -> makeWaitingFragment", LogTag.I)
                makeWaitingFragment()
            }


        }
    }

    /**
     * Guest가 생성된 대기실에 들어갈 때(accessWaitingRoom), 해당 LobbyRoom 객체를 Firebase Database에서 제거.
     * */
    fun removeLobbyRoomValue(roomKey:String) {
        viewModelScope.launch {
            lobbyRepositery.removeLobbyRoomValue(roomKey)
        }
    }


    /**
     * lobby_fragment.xml에서 R.id.btn_make_room 을 클릭할 때
     *
     * RoomSetDialog를 트리거 하는 함수
     * */
    fun onRoomMakeBtn() {
        _makingRoom.value = Event(Unit)
    }

    /**
     * RoomSetDialog에서 ok버튼을 눌렀을 때
     *
     * lobbyRoom객체를 생성하고 writeLobbyRoom()을 호출하는 함수
     * */
    fun makeRoom(name:String) {
        val roomName = name.takeIf { it.isNotEmpty() } ?: "${host.name.substringBefore('@')}(이)랑 야추 한판!"
        val new = LobbyRoom(
            host.playerId,
            roomName,
            mutableMapOf("host" to host),
            ""
        )

        writeLobbyRoom(new)
    }


    /**
     * navigate 'WaitingFragment' making waitRoom by Host
     * */
    fun makeWaitingFragment() {
        _makeWaitRoom.value = Event(Unit)
    }

    /**
     * navigate 'WaitingFragment' accessing waitRoom by guest
     * */
    fun accessWaitingFragment(lobbyRoom: LobbyRoom) {
        _accessWaitRoom.value = Event(lobbyRoom)
    }
}