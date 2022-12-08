package com.ranseo.yatchgame.data.source

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ranseo.yatchgame.LogTag
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.log
import com.ranseo.yatchgame.room.YachtRoomDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerDataSource @Inject constructor(private val yachtRoomDao: YachtRoomDao, private val firebaseDatabase: FirebaseDatabase, private val firebaseAuth: FirebaseAuth) {
    private val TAG = "PlayerDataSource"


    suspend fun getPlayer(playerId:String) : Player {
        log(TAG,"getPlayer : ${playerId}", LogTag.I)
        val player= yachtRoomDao.getPlayer(playerId)
        log(TAG,"getPlayer : ${player}", LogTag.I)
        return player
    }

    suspend fun getHostPlayer() = getPlayer(firebaseAuth.currentUser!!.uid)

    fun getMyPlayer() = yachtRoomDao.getHostPlayer(firebaseAuth.currentUser!!.uid)
    suspend fun insert(player: Player) = withContext(Dispatchers.IO){

        yachtRoomDao.insertPlayer(player)
    }

    suspend fun write(player:Player, callBack:(isWrite:Boolean)->Unit) = withContext(Dispatchers.IO){
        val path = firebaseDatabase.reference.child("player").child(player.playerId)
        path.setValue(player).addOnCompleteListener {
            if(it.isSuccessful) {
                log(TAG, "player 쓰기 성공", LogTag.I)
                callBack(true)
            } else {
                log(TAG, "player 쓰기 실패 : ${it.exception?.message}", LogTag.D)
                callBack(false)
            }
        }
    }
}