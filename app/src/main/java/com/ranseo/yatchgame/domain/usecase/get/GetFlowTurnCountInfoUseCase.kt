package com.ranseo.yatchgame.domain.usecase.get

import com.ranseo.yatchgame.data.model.TurnCountInfo
import com.ranseo.yatchgame.data.repo.GameInfoRepository
import com.ranseo.yatchgame.data.repo.TurnCountInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlowTurnCountInfoUseCase @Inject constructor(turnCountInfoRepository: TurnCountInfoRepository, gameInfoRepository: GameInfoRepository){
    private val getGameId: suspend () -> String = {
        gameInfoRepository.getGameId()
    }

    private val flowTurnCountInfoGetter : suspend (gameId:String) -> Flow<Result<TurnCountInfo>> = { gameId ->
        turnCountInfoRepository.getTurnCountInfo(gameId)
    }

    suspend operator fun invoke() : Flow<Result<TurnCountInfo>> = withContext(Dispatchers.IO) {
        flowTurnCountInfoGetter(getGameId())
    }
}