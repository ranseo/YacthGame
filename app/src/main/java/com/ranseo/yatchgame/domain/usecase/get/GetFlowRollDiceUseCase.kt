package com.ranseo.yatchgame.domain.usecase.get

import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.repo.GameInfoRepository
import com.ranseo.yatchgame.data.repo.RollDiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlowRollDiceUseCase @Inject constructor(rollDiceRepository: RollDiceRepository, gameInfoRepository: GameInfoRepository){
    private val getGameId: suspend () -> String = {
        gameInfoRepository.getGameId()
    }

    private val flowRollDiceGetter : suspend (gameId:String) -> Flow<Result<RollDice>> = { gameId ->
        rollDiceRepository.getRollDice(gameId)
    }

    suspend operator fun invoke() : Flow<Result<RollDice>> = withContext(Dispatchers.IO) {
        flowRollDiceGetter(getGameId())
    }
}