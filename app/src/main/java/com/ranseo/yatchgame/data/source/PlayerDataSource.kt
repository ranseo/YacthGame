package com.ranseo.yatchgame.data.source

import com.google.firebase.database.FirebaseDatabase
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.room.YachtRoomDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerDataSource @Inject constructor(private val yachtRoomDao: YachtRoomDao, private val firebaseDatabase: FirebaseDatabase) {
    suspend fun insert(player: Player) = withContext(Dispatchers.IO){yachtRoomDao.insertPlayer(player)}

    suspend fun write(player:Player) {
        firebaseDatabase.reference.child("player")
    }
}