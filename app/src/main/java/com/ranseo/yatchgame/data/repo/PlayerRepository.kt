package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.source.PlayerLocalDataSource
import com.ranseo.yatchgame.data.source.PlayerRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlayerRepository @Inject constructor(private val playerRemoteDataSource: PlayerRemoteDataSource, private val playerLocalDataSource: PlayerLocalDataSource) {

    //local
    val player = playerLocalDataSource.getPlayerLiveData() // LiveData

    suspend fun getPlayer() = playerLocalDataSource.getPlayer()

    suspend fun insertPlayer(player:Player) = withContext(Dispatchers.IO){
        playerLocalDataSource.insert(player)
    }

    //remote
    suspend fun writePlayer(player: Player, callBack:(isWrite:Boolean)->Unit) = withContext(Dispatchers.IO){
        playerRemoteDataSource.write(player, callBack)
    }


}