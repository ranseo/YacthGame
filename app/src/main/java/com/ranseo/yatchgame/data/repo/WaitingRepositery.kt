package com.ranseo.yatchgame.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.model.GameInfoFirebaseModel
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.model.WaitingRoom
import com.ranseo.yatchgame.data.source.GameInfoDataSource
import com.ranseo.yatchgame.data.source.LobbyRoomDataSource
import com.ranseo.yatchgame.data.source.PlayerDataSource
import com.ranseo.yatchgame.data.source.WaitingRoomDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WaitingRepositery @Inject constructor(
    private val waitingRoomDataSource: WaitingRoomDataSource,
    private val playerDataSource: PlayerDataSource,
    private val gameInfoDataSource: GameInfoDataSource
) {
    suspend fun refreshHostPlayer() =
        withContext(Dispatchers.IO) { playerDataSource.getHostPlayer() }

    suspend fun getWaitingRoom(roomId: String, callback: (waitingRoom: WaitingRoom) -> Unit) {
        waitingRoomDataSource.getWaitingRoom(roomId, callback)
    }

    suspend fun removeWaitingRoomValueEventListener(roomId: String) = withContext(Dispatchers.IO) {
        waitingRoomDataSource.removeWaitingRoomValueEventListener(roomId)
    }

    suspend fun writeWaitingRoom(waitingRoom: WaitingRoom) = withContext(Dispatchers.IO) {
        waitingRoomDataSource.writeWaitingRoom(waitingRoom)
    }

    suspend fun updateWaitingRoom(waitingRoom: WaitingRoom) = withContext(Dispatchers.IO) {
        waitingRoomDataSource.updateWaitingRoom(waitingRoom)
    }

    suspend fun removeWaitingRoomValue(roomId: String) = withContext(Dispatchers.IO) {
        waitingRoomDataSource.removeWaitingRoom(roomId)
    }

    suspend fun writeGameInfoAtFirst(gameInfo: GameInfo, callback: (flag:Boolean)->Unit) = withContext(Dispatchers.IO){
        gameInfoDataSource.writeGameInfoAtFirst(gameInfo, callback)
    }

}