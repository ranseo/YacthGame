package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.data.source.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GamePlayRepositery @Inject constructor(
    private val gameInfoFirebaseDataSource: GameInfoFirebaseDataSource,
    private val gameInfoRoomDataSource: GameInfoRoomDataSource,
    private val playerDataSource: PlayerDataSource,
    private val rollDiceDataSource: RollDiceDataSource,
    private val boardInfoDataSource: BoardInfoDataSource,
    private val emojiInfoRepositery: EmojiInfoRepositery,
    private val turnCountInfoRepositery: TurnCountInfoRepositery
) {

    val player = playerDataSource.getMyPlayer()
    //Player
    suspend fun refreshHostPlayer() =
        withContext(Dispatchers.IO) { playerDataSource.getHostPlayer() }

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

    ///RollDice

    suspend fun writeRollDice(rollDice: RollDice) = withContext(Dispatchers.IO) {
        rollDiceDataSource.writeRollDice(rollDice)
    }

    suspend fun getRollDice(gameId:String, callback:(rollDice:RollDice)->Unit) = withContext(Dispatchers.IO){
        rollDiceDataSource.getRollDice(gameId, callback)
    }

    //BoardInfo

    suspend fun writeBoardInfo(gameId:String, boardInfo: BoardInfo) = withContext(Dispatchers.IO){
        boardInfoDataSource.writeBoardInfo(gameId, boardInfo)
    }

    suspend fun getBoardInfo(gameId: String) : Flow<Result<BoardInfo>> = withContext(Dispatchers.IO) {
        boardInfoDataSource.getBoardInfo(gameId)
    }


    suspend fun removeListener(gameId:String) = withContext(Dispatchers.IO){
        rollDiceDataSource.removeValueEventListener(gameId)
        gameInfoFirebaseDataSource.removeValueEventListener(gameId)
    }


    //Emoji
    suspend fun getEmojiInfo(gameId: String, playerId:String) : Flow<Result<EmojiInfo>> = withContext(Dispatchers.IO) {
        emojiInfoRepositery.getEmojiInfo(gameId, playerId)
    }

    suspend fun writeEmojiInfo(gameId:String, playerId:String, emojiInfo: EmojiInfo) {
        emojiInfoRepositery.setEmojiInfo(gameId, playerId, emojiInfo)
    }

    //TurnCount
    suspend fun getTurnCountInfo(gameId:String) : Flow<Result<TurnCountInfo>> = withContext(Dispatchers.IO){
        turnCountInfoRepositery.getTurnCountInfo(gameId)
    }

    suspend fun writeTurnCountInfo(gameId: String, turnCountInfo: TurnCountInfo) = withContext(Dispatchers.IO) {
        turnCountInfoRepositery.writeTurnCountInfo(gameId,turnCountInfo)
    }

}