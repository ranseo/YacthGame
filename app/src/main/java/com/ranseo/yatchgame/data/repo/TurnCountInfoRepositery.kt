package com.ranseo.yatchgame.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ranseo.yatchgame.data.model.TurnCountInfo
import com.ranseo.yatchgame.data.source.TurnCountDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TurnCountInfoRepositery @Inject constructor(private val turnCountInfoDataSource: TurnCountDataSource) {


    suspend fun getTurnCountInfo(gameId:String) : Flow<Result<TurnCountInfo>> = withContext(Dispatchers.IO) {
        turnCountInfoDataSource.getTurnCountInfo(gameId)
    }
    suspend fun writeTurnCountInfo(gameId:String, turnCountInfo: TurnCountInfo) = withContext(Dispatchers.IO) {
        turnCountInfoDataSource.writeTurnCountInfo(gameId, turnCountInfo)
    }
}