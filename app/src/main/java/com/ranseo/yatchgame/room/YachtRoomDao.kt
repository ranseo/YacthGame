package com.ranseo.yatchgame.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.model.gameinfo.MyGameScoreAndPlayer

@Dao
interface YachtRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)

    @Update
    suspend fun updatePlayer(player: Player)

    @Query("DELETE FROM player_table WHERE playerId = :playerId")
    suspend fun deletePlayer(playerId:Int)

//    @Query("SELECT * FROM player_table WHERE playerId = :playerId")
//    suspend fun getPlayer(playerId:String) : Player

    @Query("SELECT * FROM player_table WHERE playerId = :playerId")
    fun getPlayerLiveData(playerId:String) : LiveData<Player>

    @Query("SELECT * FROM player_table WHERE playerId = :playerId")
    suspend fun getPlayer(playerId:String) : Player?

    //

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameInfo(gameInfo: GameInfo)

    @Update
    suspend fun updateGameInfo(gameInfo: GameInfo)

    @Query("DELETE FROM game_info_table WHERE gameId = :gameInfoId")
    suspend fun deletGameInfo(gameInfoId: Int)

    @Query("SELECT gameId FROM game_info_table ORDER BY game_start_time DESC LIMIT 1")
    suspend fun getGameInfoGameId() : String

    @Query("SELECT game_start_time FROM game_info_table ORDER BY game_start_time DESC LIMIT 1")
    suspend fun getGameInfoGameStartTime() : String

    @Query("SELECT * FROM game_info_table WHERE :player = first or :player = second")
    fun getGameInfos(player:Player) : LiveData<List<GameInfo>>

    //Statis

    @Query("SELECT result AS gameResult, gameScore, first AS firstPlayer, second AS secondPlayer, boards FROM game_info_table AS g WHERE :player = g.first OR :player = g.second")
    fun getMyGameScoreAndPlayer(player:Player) : LiveData<List<MyGameScoreAndPlayer>>

}