package com.ranseo.yatchgame.domain.usecase

import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import com.ranseo.yatchgame.data.repo.EmojiInfoRepository
import com.ranseo.yatchgame.data.repo.GameInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WriteBoardInfoUseCase @Inject constructor(
    boardInfoRepository: BoardInfoRepository,
    gameInfoRepository: GameInfoRepository
) {
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val getGameId: suspend () -> String = {
        gameInfoRepository.getGameId()
    }
    private val boardInfoWriter: suspend (gameId:String,boardInfo: BoardInfo) -> Unit =
        { gameId, boardInfo ->
            boardInfoRepository.writeBoardInfo(gameId, boardInfo)
        }

    suspend operator fun invoke(gameId:String ,boardInfo:BoardInfo) =
        withContext(defaultDispatcher) {
            boardInfoWriter(gameId,boardInfo)
        }

    suspend operator fun invoke(boardInfo:BoardInfo) =
        withContext(defaultDispatcher) {
            boardInfoWriter(getGameId(),boardInfo)
        }
}