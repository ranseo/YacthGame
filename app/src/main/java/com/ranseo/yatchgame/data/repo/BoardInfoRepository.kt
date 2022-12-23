package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.source.BoardInfoDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BoardInfoRepository @Inject constructor(private val boardInfoDataSource: BoardInfoDataSource) {
    //BoardInfo
    suspend fun writeBoardInfo(gameId:String, boardInfo: BoardInfo) = withContext(Dispatchers.IO){
        boardInfoDataSource.writeBoardInfo(gameId, boardInfo)
    }

    suspend fun getBoardInfo(gameId: String) : Flow<Result<BoardInfo>> = withContext(Dispatchers.IO) {
        boardInfoDataSource.getBoardInfo(gameId)
    }

}