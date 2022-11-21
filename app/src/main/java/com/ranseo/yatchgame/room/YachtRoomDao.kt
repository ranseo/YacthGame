package com.ranseo.yatchgame.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.model.Player

@Dao
interface YachtRoomDao {
    @Insert
    suspend fun insertPlayer(player: Player)

    @Update
    suspend fun updatePlayer(player: Player)

    @Query("DELETE FROM player_table WHERE playerId = :playerId")
    suspend fun deletePlayer(playerId:Int)

    @Query("SELECT * FROM player_table WHERE playerId = :playerId")
    suspend fun getPlayer(playerId:String) : Player

    //

    @Insert
    suspend fun insertGameInfo(gameInfo: GameInfo)

    @Update
    suspend fun updateGameInfo(gameInfo: GameInfo)

    @Query("DELETE FROM game_info_table WHERE gameId = :gameInfoId")
    suspend fun deletGameInfo(gameInfoId: Int)
}