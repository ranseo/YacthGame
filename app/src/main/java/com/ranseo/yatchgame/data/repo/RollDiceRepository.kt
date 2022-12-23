package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.source.RollDiceDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RollDiceRepository @Inject constructor(private val rollDiceDataSource: RollDiceDataSource) {
    ///RollDice
    suspend fun writeRollDice(rollDice: RollDice) = withContext(Dispatchers.IO) {
        rollDiceDataSource.writeRollDice(rollDice)
    }

    suspend fun getRollDice(gameId: String): Flow<Result<RollDice>> = withContext(Dispatchers.IO) {
        rollDiceDataSource.getRollDice(gameId)
    }
}