package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.log.LogModel
import com.ranseo.yatchgame.data.source.LogModelDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogModelRepository @Inject constructor(private val logModelDataSource: LogModelDataSource) {
    suspend fun writeLog(log:LogModel) = withContext(Dispatchers.IO){
        logModelDataSource.writeLog(log)
    }
}