package com.ranseo.yatchgame.data.source

import androidx.room.Database
import com.google.firebase.database.FirebaseDatabase
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.room.YachtRoomDao
import com.ranseo.yatchgame.util.DateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class GameInfoDataSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase, private val yachtRoomDao: YachtRoomDao) {
    private val TAG = "GameInfoDataSource"


    /**
     * 가장 최근 GameInfo의 GameId를 반환
     * */
    suspend fun getGameInfoGameId() = withContext(Dispatchers.IO) {
        yachtRoomDao.getGameInfoGameId()
    }

    /**
     * Room Database에 GameInfo 객체 Insert
     * */
    suspend fun insertGameInfo(gameInfo: GameInfo) = withContext(Dispatchers.IO){
        yachtRoomDao.insertGameInfo(gameInfo)
    }

    /**
     * Firebase Database에 GameInfo 데이터 write
     * */
    suspend fun writeGameInfo(gameInfo: GameInfo) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("gameInfo").child(gameInfo.gameId)
        val new = gameInfo.asFirebaseModel()
        ref.setValue(new).addOnCompleteListener {
            if(it.isSuccessful) {
                log(TAG,"writeGameInfo Success : ${gameInfo}", LogTag.I)
            } else {
                log(TAG,"writeGameInfo Failure", LogTag.D)
            }
        }
    }

    /**
     * Firebase Database에 GameInfo 데이터 write
     * */
    suspend fun writeGameInfoAtFirst(waitingRoom: WaitingRoom, callback:(flag:Boolean, gameInfo:GameInfo)->Unit) = withContext(Dispatchers.IO) {
        val ref = firebaseDatabase.reference.child("gameInfo").child(waitingRoom.waitingKey)

        val new =GameInfo(waitingRoom, DateTime.getNowDate(System.currentTimeMillis()), listOf(Board(), Board()))
        val firebaseModel = new.asFirebaseModel()
        ref.setValue(firebaseModel).addOnCompleteListener {
            if(it.isSuccessful) {
                log(TAG,"writeGameInfo Success : ${new}", LogTag.I)
                callback(true, new)
            } else {
                log(TAG,"writeGameInfo Failure", LogTag.D)
                callback(false,new)
            }
        }
    }



}