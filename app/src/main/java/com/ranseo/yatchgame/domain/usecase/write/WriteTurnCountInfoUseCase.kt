package com.ranseo.yatchgame.domain.usecase.write

import com.ranseo.yatchgame.data.model.TurnCountInfo
import com.ranseo.yatchgame.data.repo.GameInfoRepository
import com.ranseo.yatchgame.data.repo.TurnCountInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WriteTurnCountInfoUseCase @Inject constructor(
    turnCountInfoRepository: TurnCountInfoRepository,
    gameInfoRepository: GameInfoRepository
) {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val getGameId: suspend () -> String = {
        gameInfoRepository.getGameId()
    }

    private val turnCountInfoWriter: suspend (gameId: String, turnCountInfo: TurnCountInfo) -> Unit =
        { gameId, turnCountInfo ->
            turnCountInfoRepository.writeTurnCountInfo(gameId, turnCountInfo)
        }

    suspend operator fun invoke(turnCountInfo: TurnCountInfo) =
        withContext(defaultDispatcher) {
            turnCountInfoWriter(getGameId(), turnCountInfo)
        }

    suspend operator fun invoke(gameId: String, turnCountInfo: TurnCountInfo) =
        withContext(defaultDispatcher) {
            turnCountInfoWriter(gameId, turnCountInfo)
        }
}