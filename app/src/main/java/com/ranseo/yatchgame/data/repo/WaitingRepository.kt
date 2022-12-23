package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.model.WaitingRoom
import com.ranseo.yatchgame.data.source.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WaitingRepository @Inject constructor(
    private val waitingRoomDataSource: WaitingRoomDataSource,
    private val playerDataSource: PlayerRemoteDataSource,
    private val gameInfoFirebaseDataSource: GameInfoFirebaseDataSource,
    private val gameInfoRoomDataSource: GameInfoRoomDataSource
) {
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

    suspend fun writeGameInfoAtFirst(waitingRoom: WaitingRoom, callback: (flag:Boolean, gameInfo:GameInfo)->Unit) = withContext(Dispatchers.IO){
        gameInfoFirebaseDataSource.writeGameInfoAtFirst(waitingRoom, callback)
    }

    suspend fun insertGameInfoAtFirst(gameInfo: GameInfo) {
        gameInfoRoomDataSource.insertGameInfo(gameInfo)
    }
}