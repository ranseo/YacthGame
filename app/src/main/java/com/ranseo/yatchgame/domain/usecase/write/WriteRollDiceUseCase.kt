package com.ranseo.yatchgame.domain.usecase.write

import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.repo.RollDiceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WriteRollDiceUseCase @Inject constructor(
    rollDiceRepository: RollDiceRepository,
) {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val turnCountInfoWriter: suspend (rollDice: RollDice) -> Unit =
        { rollDice ->
            rollDiceRepository.writeRollDice(rollDice)
        }

    suspend operator fun invoke(rollDice: RollDice) =
        withContext(defaultDispatcher) {
            turnCountInfoWriter(rollDice)
        }
}