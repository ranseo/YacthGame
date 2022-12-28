package com.ranseo.yatchgame.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.ranseo.yatchgame.data.model.LobbyRoom
import com.ranseo.yatchgame.data.source.LobbyRoomDataSource
import com.ranseo.yatchgame.data.source.PlayerRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class LobbyRoomRepository @Inject constructor(
    private val lobbyRoomDataSource: LobbyRoomDataSource,
) {
    private val TAG = "LobbyRepositery"

    suspend fun getLobbyRooms(callback: (list: List<LobbyRoom>) -> Unit) =
        withContext(Dispatchers.IO) {
            lobbyRoomDataSource.getLobbyRooms(callback)
        }

    suspend fun removeEventListener() = withContext(Dispatchers.IO) {
        lobbyRoomDataSource.removeLobbyRoomsValueEventListener()
    }

    suspend fun writeLobbyRoom(lobbyRoom: LobbyRoom, callback: (roomKey:String) -> Unit) = withContext(Dispatchers.IO) {
        lobbyRoomDataSource.writeLobbyRoom(lobbyRoom, callback)
    }

    suspend fun removeLobbyRoomValue(roomKey:String) = withContext(Dispatchers.IO) {
        lobbyRoomDataSource.removeLobbyRoom(roomKey)
    }
}