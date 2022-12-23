package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.data.source.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GamePlayRepositery @Inject constructor(
    private val gameInfoFirebaseDataSource: GameInfoFirebaseDataSource,
    private val gameInfoRoomDataSource: GameInfoRoomDataSource,
) {

    //GameInfo
    suspend fun getGameId() =
        withContext(Dispatchers.IO) { gameInfoRoomDataSource.getGameInfoGameId() }

    suspend fun getGameStartTime() = withContext(Dispatchers.IO) {
        gameInfoRoomDataSource.getGameInfoGameStartTime()
    }

    suspend fun getGameInfo(gameInfoId: String, callback: (gameInfo: GameInfo) -> Unit) =
        withContext(Dispatchers.IO) { gameInfoFirebaseDataSource.getGameInfo(gameInfoId, callback) }

    suspend fun insertFirstGameInfo(gameInfo: GameInfo) = withContext(Dispatchers.IO) {
        gameInfoRoomDataSource.insertGameInfo(gameInfo)
    }

    suspend fun updateGameInfo(gameInfo: GameInfo) =
        withContext(Dispatchers.IO) { gameInfoRoomDataSource.updateGameInfo(gameInfo) }

    suspend fun writeGameInfo(gameInfo: GameInfo) =
        withContext(Dispatchers.IO) {gameInfoFirebaseDataSource.writeGameInfo(gameInfo)}



    suspend fun removeListener(gameId:String) = withContext(Dispatchers.IO){
        gameInfoFirebaseDataSource.removeValueEventListener(gameId)
    }
}