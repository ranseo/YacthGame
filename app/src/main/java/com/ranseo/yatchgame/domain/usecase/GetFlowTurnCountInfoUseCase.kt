package com.ranseo.yatchgame.domain.usecase

import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.model.TurnCountInfo
import com.ranseo.yatchgame.data.repo.RollDiceRepository
import com.ranseo.yatchgame.data.repo.TurnCountInfoRepositery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlowTurnCountInfoUseCase @Inject constructor(turnCountInfoRepositery: TurnCountInfoRepositery){
    private val flowTurnCountInfoGetter : suspend (gameId:String) -> Flow<Result<TurnCountInfo>> = { gameId ->
        turnCountInfoRepositery.getTurnCountInfo(gameId)
    }

    suspend operator fun invoke(gameId: String) : Flow<Result<TurnCountInfo>> = withContext(Dispatchers.IO) {
        flowTurnCountInfoGetter(gameId)
    }
}