package com.ranseo.yatchgame.domain.usecase

import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlowBoardInfoUseCase @Inject constructor(boardInfoRepository: BoardInfoRepository){
    private val flowBoardInfoGetter : suspend (gameId:String) -> Flow<Result<BoardInfo>> = { gameId ->
        boardInfoRepository.getBoardInfo(gameId)
    }

    suspend operator fun invoke(gameId: String) : Flow<Result<BoardInfo>> = withContext(Dispatchers.IO) {
        flowBoardInfoGetter(gameId)
    }
}