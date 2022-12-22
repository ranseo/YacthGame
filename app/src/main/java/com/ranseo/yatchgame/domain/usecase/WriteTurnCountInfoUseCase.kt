package com.ranseo.yatchgame.domain.usecase

import com.ranseo.yatchgame.data.model.TurnCountInfo
import com.ranseo.yatchgame.data.repo.TurnCountInfoRepositery
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WriteTurnCountInfoUseCase @Inject constructor(
    turnCountInfoRepositery: TurnCountInfoRepositery,
) {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val turnCountInfoWriter: suspend (gameId: String, turnCountInfo: TurnCountInfo) -> Unit =
        { gameId, turnCountInfo ->
            turnCountInfoRepositery.writeTurnCountInfo(gameId, turnCountInfo)
        }

    suspend operator fun invoke(gameId: String, turnCountInfo: TurnCountInfo) =
        withContext(defaultDispatcher) {
            turnCountInfoWriter(gameId, turnCountInfo)
        }
}