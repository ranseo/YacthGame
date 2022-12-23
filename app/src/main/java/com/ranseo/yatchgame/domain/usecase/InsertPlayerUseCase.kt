package com.ranseo.yatchgame.domain.usecase

import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.repo.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InsertPlayerUseCase @Inject constructor(playerRepository: PlayerRepository) {
    private val playerInserter: suspend (player: Player) -> Unit =
        { player->
            playerRepository.insertPlayer(player)
        }

    suspend operator fun invoke(player: Player) =
        withContext(Dispatchers.IO) {
            playerInserter(player)
        }
}