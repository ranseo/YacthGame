package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.data.source.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameInfoRepository @Inject constructor(
    private val gameInfoRemoteDataSource: GameInfoRemoteDataSource,
    private val gameInfoLocalDataSource: GameInfoLocalDataSource,
) {

    //GameInfo
    suspend fun getGameId() =
        withContext(Dispatchers.IO) { gameInfoLocalDataSource.getGameInfoGameId() }

    suspend fun getGameStartTime() = withContext(Dispatchers.IO) {
        gameInfoLocalDataSource.getGameInfoGameStartTime()
    }

    suspend fun getGameInfo(gameInfoId: String) : Flow<Result<GameInfo>> =
        withContext(Dispatchers.IO) { gameInfoRemoteDataSource.getGameInfo(gameInfoId) }


    suspend fun updateGameInfo(gameInfo: GameInfo) =
        withContext(Dispatchers.IO) { gameInfoLocalDataSource.updateGameInfo(gameInfo) }

    suspend fun writeGameInfo(gameInfo: GameInfo) =
        withContext(Dispatchers.IO) {gameInfoRemoteDataSource.writeGameInfo(gameInfo)}

}