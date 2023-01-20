package com.ranseo.yatchgame.data.source

import androidx.lifecycle.LiveData
import androidx.room.Database
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.room.YachtRoomDao
import com.ranseo.yatchgame.util.DateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GameInfoLocalDataSource @Inject constructor(private val yachtRoomDao: YachtRoomDao) {
    private val TAG = "GameInfoRoomDataSource"

    /**
     * Room Database에서 가장 최근 GameInfo의 GameId를 반환
     * */
    suspend fun getGameInfoGameId() = withContext(Dispatchers.IO) {
        yachtRoomDao.getGameInfoGameId()

    }

    suspend fun getGameInfoGameStartTime() = withContext(Dispatchers.IO) {
        val a= yachtRoomDao.getGameInfoGameStartTime()
        log(TAG,"getGameInfoGameStartTime() : $a ", LogTag.I)
    }

    /**
     * Room Database에 GameInfo 객체 Insert
     * */
    suspend fun insertGameInfo(gameInfo: GameInfo) = withContext(Dispatchers.IO){
        yachtRoomDao.insertGameInfo(gameInfo)
    }

    /**
     * Room Database에 GameInfo 객체 Update
     * */
    suspend fun updateGameInfo(gameInfo: GameInfo) = withContext(Dispatchers.IO) {
        yachtRoomDao.updateGameInfo(gameInfo)
    }

    /**
     * Room Database에서 get
     * */
    fun getGameInfos(player:Player) : LiveData<List<GameInfo>> = yachtRoomDao.getGameInfos(player)

}