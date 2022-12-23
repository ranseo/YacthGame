package com.ranseo.yatchgame.domain.usecase

import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.repo.RollDiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlowRollDiceUseCase @Inject constructor(rollDiceRepository: RollDiceRepository){
    private val flowRollDiceGetter : suspend (gameId:String) -> Flow<Result<RollDice>> = { gameId ->
        rollDiceRepository.getRollDice(gameId)
    }

    suspend operator fun invoke(gameId: String) : Flow<Result<RollDice>> = withContext(Dispatchers.IO) {
        flowRollDiceGetter(gameId)
    }
}