package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.source.GameInfoFirebaseDataSource
import com.ranseo.yatchgame.data.source.GameInfoRoomDataSource
import com.ranseo.yatchgame.data.source.PlayerDataSource
import com.ranseo.yatchgame.data.source.RollDiceDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GamePlayRepositery @Inject constructor(
    private val gameInfoFirebaseDataSource: GameInfoFirebaseDataSource,
    private val gameInfoRoomDataSource: GameInfoRoomDataSource,
    private val playerDataSource: PlayerDataSource,
    private val rollDiceDataSource: RollDiceDataSource
) {

    suspend fun getGameId() =
        withContext(Dispatchers.IO) { gameInfoRoomDataSource.getGameInfoGameId() }

    suspend fun getGameInfo(gameInfoId: String, callback: (gameInfo: GameInfo) -> Unit) =
        withContext(Dispatchers.IO) { gameInfoFirebaseDataSource.getGameInfo(gameInfoId, callback) }

    suspend fun refreshHostPlayer() =
        withContext(Dispatchers.IO) { playerDataSource.getHostPlayer() }

    suspend fun insertFirstGameInfo(gameInfo: GameInfo) = withContext(Dispatchers.IO) {
        gameInfoRoomDataSource.insertGameInfo(gameInfo)
    }

    suspend fun updateGameInfo(gameInfo: GameInfo) =
        withContext(Dispatchers.IO) { gameInfoRoomDataSource.updateGameInfo(gameInfo) }

    ///

    suspend fun writeRollDice(rollDice: RollDice) = withContext(Dispatchers.IO) {
        rollDiceDataSource.writeRollDice(rollDice)
    }

    suspend fun getRollDice(gameId:String, callback:(rollDice:RollDice)->Unit) = withContext(Dispatchers.IO){
        rollDiceDataSource.getRollDice(gameId, callback)
    }

    suspend fun removeListener(gameId:String) = withContext(Dispatchers.IO){
        rollDiceDataSource.removeValueEventListener(gameId)
        gameInfoFirebaseDataSource.removeValueEventListener(gameId)
    }

}