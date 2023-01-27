package com.ranseo.yatchgame.data.source

import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.room.YachtRoomDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MyGameScoreAndPlayerLocalDataSource @Inject constructor(private val yachtRoomDao: YachtRoomDao) {
    fun getMyGameScoreAndPlayer(player:Player) = yachtRoomDao.getMyGameScoreAndPlayer(player)

}