package com.ranseo.yatchgame.domain.usecase.get

import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import com.ranseo.yatchgame.data.repo.GameInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlowGameInfoUseCase @Inject constructor(gameInfoRepository: GameInfoRepository) {
    private val getGameId: suspend () -> String = {
        gameInfoRepository.getGameId()
    }

    private val flowGameInfoGetter: suspend (gameId: String) -> Flow<Result<GameInfo>> = { gameId ->
        gameInfoRepository.getGameInfo(gameId)
    }

    suspend operator fun invoke(): Flow<Result<GameInfo>> =
        withContext(Dispatchers.IO) {
            flowGameInfoGetter(getGameId())
        }
}
