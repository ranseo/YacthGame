package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.Rematch
import com.ranseo.yatchgame.data.source.RematchDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RematchRepository @Inject constructor(private val rematchDataSource: RematchDataSource){
    suspend fun getRematch(uid:String) = withContext(Dispatchers.IO){
        rematchDataSource.getRematch(uid)
    }

    suspend fun writeRematch(rematch: Rematch) : Flow<Result<String>> = withContext(Dispatchers.IO){
        rematchDataSource.writeRematch(rematch)
    }
}