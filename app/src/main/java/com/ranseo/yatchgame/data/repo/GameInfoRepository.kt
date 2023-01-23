package com.ranseo.yatchgame.data.repo

import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.data.model.gameinfo.MyGameScoreAndPlayer
import com.ranseo.yatchgame.data.model.statis.BestScore
import com.ranseo.yatchgame.data.model.statis.WinDrawLose
import com.ranseo.yatchgame.data.source.*
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.ui.game.GamePlayFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameInfoRepository @Inject constructor(
    private val gameInfoRemoteDataSource: GameInfoRemoteDataSource,
    private val gameInfoLocalDataSource: GameInfoLocalDataSource,
    private val myGameScoreAndPlayerLocalDataSource: MyGameScoreAndPlayerLocalDataSource
) {

    fun getGameInfos(player: Player): LiveData<List<GameInfo>> =
        gameInfoLocalDataSource.getGameInfos(player)

    //GameInfo
    suspend fun getGameId() =
        withContext(Dispatchers.IO) { gameInfoLocalDataSource.getGameInfoGameId() }

    suspend fun getGameStartTime() = withContext(Dispatchers.IO) {
        gameInfoLocalDataSource.getGameInfoGameStartTime()
    }

    suspend fun getGameInfo(gameInfoId: String): Flow<Result<GameInfo>> =
        withContext(Dispatchers.IO) { gameInfoRemoteDataSource.getGameInfo(gameInfoId) }


    suspend fun updateGameInfo(gameInfo: GameInfo) =
        withContext(Dispatchers.IO) { gameInfoLocalDataSource.updateGameInfo(gameInfo) }

    suspend fun writeGameInfo(gameInfo: GameInfo) =
        withContext(Dispatchers.IO) { gameInfoRemoteDataSource.writeGameInfo(gameInfo) }

    //
    fun getWinDrawLose(player: Player): LiveData<WinDrawLose> =
        Transformations.map(myGameScoreAndPlayerLocalDataSource.getMyGameScoreAndPlayer(player)) { list ->
            var draw: Int = 0
            var win: Int = 0
            var lose: Int = 0
            try {
                for ((gameResult, gameScore, firstPlayer, secondPlayer, _) in list) {

                    log(
                        TAG,
                        "gameResult : ${gameResult}, gameScore : ${gameScore}, firstPlayer : ${firstPlayer}, secondPlayer : ${secondPlayer}",
                        LogTag.I
                    )
                    if (gameResult.isEmpty() || gameScore.isEmpty()) continue
                    if (DRAW_FLAG.containsMatchIn(gameResult)) {
                        log(TAG, "Draw!", LogTag.I)
                        draw++
                    } else if (EARLY_FLAG.containsMatchIn(gameResult)) {
                        val runaway =
                            gameResult.substringBefore("님이 게임에서 나가셨습니다.").drop(1).dropLast(1)
                        if ( (firstPlayer.name == runaway && firstPlayer.playerId == player.playerId) || (secondPlayer.name == runaway && secondPlayer.playerId == player.playerId)) {
                            lose++
                            log(TAG, "Early! lose!", LogTag.I)
                        } else {
                            win++
                            log(TAG, "Early! win!", LogTag.I)
                        }
                    } else {
                        log(TAG,"win!", LogTag.I)
                        val firstScore = gameScore.substringBefore(" :").toInt()
                        val secondScore = gameScore.substringAfter(": ").toInt()

                        if (firstPlayer.playerId == player.playerId) {
                            if (firstScore > secondScore) win++ else lose++
                        } else {
                            if (firstScore < secondScore) win++ else lose++
                        }
                    }
                }

                WinDrawLose(win, draw, lose)
            } catch (error: Exception) {
                log(TAG, "getMyGameScoreAndPlayer error = ${error.message}", LogTag.I)
                WinDrawLose(win, draw, lose)
            }
        }


    fun getBestScore(player: Player) : LiveData<BestScore> =
        Transformations.map(myGameScoreAndPlayerLocalDataSource.getMyGameScoreAndPlayer(player)) { list ->
            var bestScore = 0
            var boards: List<Board>? = null
            var firstPlayer :Player? = null
            var secondPlayer:Player? = null

            try {
                val gameScoreList : List<MyGameScoreAndPlayer> = list.filter { it.gameScore.isNotEmpty()}

                val first : MyGameScoreAndPlayer? = gameScoreList.maxByOrNull { it.gameScore.substringBefore(" :").toInt()}
                val second : MyGameScoreAndPlayer? = gameScoreList.maxByOrNull { it.gameScore.substringAfter(": ").toInt()}

                if(first != null && first.firstPlayer.playerId == player.playerId) {
                    bestScore = first.gameScore.substringBefore(" :").toInt()
                    boards = first.boards
                    firstPlayer= first.firstPlayer
                    secondPlayer = first.secondPlayer
                    log(TAG,"getBestScore() : first ${BestScore(bestScore, boards, firstPlayer, secondPlayer)}",LogTag.I)
                } else if(second != null && second.secondPlayer.playerId == player.playerId) {
                    bestScore = second.gameScore.substringAfter(": ").toInt()
                    boards = second.boards
                    firstPlayer = second.firstPlayer
                    secondPlayer = second.secondPlayer
                    log(TAG,"getBestScore() : second ${BestScore(bestScore, boards, firstPlayer, secondPlayer)}",LogTag.I)
                }

                BestScore(bestScore, boards, firstPlayer, secondPlayer)
            } catch (error:Exception) {
                log(TAG,"getBestScore() Exception : ${error.message}",LogTag.I)
                BestScore(bestScore, null, firstPlayer, secondPlayer )
            } catch (error:NullPointerException) {
                log(TAG,"getBestScore() NullPointerException : ${error.message}",LogTag.I)
                BestScore(bestScore, null, firstPlayer, secondPlayer )
            }
        }


    companion object {
        private val TAG = "GameInfoRepository"
        private val EARLY_FLAG = Regex("님이 게임에서 나가셨습니다.")
        private val DRAW_FLAG = Regex("치열한 경기!\n무승부 입니다.")
    }
}