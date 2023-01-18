package com.ranseo.yatchgame.data.repo

import androidx.core.content.ContextCompat
import androidx.lifecycle.Transformations
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.data.source.*
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

    //
    suspend fun getMyGameScoreAndPlayer(player: Player) = withContext(Dispatchers.Default) {
        Transformations.map(myGameScoreAndPlayerLocalDataSource.getMyGameScoreAndPlayer(player)) { list ->

            var draw : Int = 0
            var win : Int = 0
            var lose : Int = 0
            for((gameResult, first, second) in list) {
                if(DRAW_FLAG.containsMatchIn(gameResult)) draw++
                val playerName = gameResult.substringAfter(WIN_FLAG).substringBefore("님")
                if(first != null) {

                } else {

                }


            }
        }
    }

    companion object {

        private val WIN_FLAG = "\n"
        private val DRAW_FLAG = Regex("치열한 경기!\n무승부 입니다.")
    }
}