package com.ranseo.yatchgame.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.LobbyRoom
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.source.LobbyRoomDataSource
import com.ranseo.yatchgame.data.source.PlayerDataSource
import com.ranseo.yatchgame.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class LobbyRepositery @Inject constructor(
    private val lobbyRoomDataSource: LobbyRoomDataSource,
    private val playerDataSource: PlayerDataSource,
    private val firebaseAuth: FirebaseAuth
) {
    private val TAG = "LobbyRepositery"

    suspend fun refreshHostPlayer() = withContext(Dispatchers.IO) {playerDataSource.getHostPlayer()}

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