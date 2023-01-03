package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.RematchResult
import com.ranseo.yatchgame.data.source.RematchResultDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RematchResultRepository @Inject constructor(private val rematchResultDataSource: RematchResultDataSource) {

    suspend fun getRematchResult(uid:String) : Flow<Result<RematchResult>> = withContext(Dispatchers.IO) {
        rematchResultDataSource.getRematchResult(uid)
    }

}