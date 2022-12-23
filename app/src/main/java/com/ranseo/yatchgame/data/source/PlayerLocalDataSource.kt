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
class PlayerLocalDataSource @Inject constructor(private val yachtRoomDao: YachtRoomDao, private val firebaseDatabase: FirebaseDatabase, private val firebaseAuth: FirebaseAuth) {
    private val TAG = "PlayerLocalDataSource"

    fun getPlayer() = yachtRoomDao.getPlayer(firebaseAuth.currentUser?.uid ?: "")
    suspend fun insert(player: Player) = withContext(Dispatchers.IO){
        yachtRoomDao.insertPlayer(player)
    }

}