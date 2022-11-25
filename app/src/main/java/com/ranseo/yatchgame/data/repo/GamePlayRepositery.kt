package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.source.GameInfoDataSource
import com.ranseo.yatchgame.data.source.PlayerDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GamePlayRepositery @Inject constructor(private val gameInfoDataSource: GameInfoDataSource, private val playerDataSource: PlayerDataSource) {

    suspend fun getGameId() = withContext(Dispatchers.IO) {gameInfoDataSource.getGameInfoGameId()}

    suspend fun refreshHostPlayer() = withContext(Dispatchers.IO) {playerDataSource.getHostPlayer()}

    suspend fun insertFirstGameInfo(gameInfo: GameInfo) = withContext(Dispatchers.IO){
        gameInfoDataSource.insertGameInfo(gameInfo)
    }
}