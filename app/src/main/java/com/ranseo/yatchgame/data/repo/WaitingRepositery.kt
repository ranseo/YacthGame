package com.ranseo.yatchgame.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.model.WaitingRoom
import com.ranseo.yatchgame.data.source.LobbyRoomDataSource
import com.ranseo.yatchgame.data.source.PlayerDataSource
import com.ranseo.yatchgame.data.source.WaitingRoomDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WaitingRepositery @Inject constructor(
    private val waitingRoomDataSource: WaitingRoomDataSource,
    private val playerDataSource: PlayerDataSource,
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun refreshHostPlayer() = getPlayer(firebaseAuth.currentUser!!.uid)

    private suspend fun getPlayer(playerId: String): Player {
        return playerDataSource.getPlayer(playerId)
    }

    suspend fun getWaitingRoom(roomId:String, callback: (waitingRoom:WaitingRoom)->Unit) {
        waitingRoomDataSource.getWaitingRoom(roomId, callback)
    }

    suspend fun writeWaitingRoom(waitingRoom: WaitingRoom) = withContext(Dispatchers.IO) {
        waitingRoomDataSource.writeWaitingRoom(waitingRoom)
    }

    suspend fun updateWaitingRoom(waitingRoom: WaitingRoom) = withContext(Dispatchers.IO) {
        waitingRoomDataSource.updateWaitingRoom(waitingRoom)
    }
}