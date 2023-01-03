package com.ranseo.yatchgame.domain.usecase.get

import com.ranseo.yatchgame.data.model.RematchResult
import com.ranseo.yatchgame.data.repo.PlayerRepository
import com.ranseo.yatchgame.data.repo.RematchResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlowResultMatchUseCase @Inject constructor(rematchResultRepository: RematchResultRepository, playerRepository: PlayerRepository){
    private val getPlayerId: suspend () -> String = {
        playerRepository.getPlayer().playerId
    }

    private val flowRematchResultGetter : suspend (playerId:String) -> Flow<Result<RematchResult>> = { playerId ->
        rematchResultRepository.getRematchResult(playerId)
    }

    suspend operator fun invoke() : Flow<Result<RematchResult>> = withContext(Dispatchers.IO) {
        flowRematchResultGetter(getPlayerId())
    }
}