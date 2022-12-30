package com.ranseo.yatchgame.domain.usecase.remove

import com.ranseo.yatchgame.data.repo.PlayerRepository
import com.ranseo.yatchgame.data.repo.RematchRepository
import javax.inject.Inject

class RemoveRematchUseCase @Inject constructor(rematchRepository: RematchRepository, playerRepository: PlayerRepository) {
    private val getPlayerId : suspend ()->String = {
        playerRepository.getPlayer().playerId
    }

    private val rematchRemover : suspend (uid:String) -> Unit = { uid ->
        rematchRepository.removeRematch(uid)
    }

    suspend  operator fun invoke() {
        rematchRemover(getPlayerId())
    }
}