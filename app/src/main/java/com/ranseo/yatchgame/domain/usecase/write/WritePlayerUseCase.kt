package com.ranseo.yatchgame.domain.usecase.write

import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.repo.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WritePlayerUseCase @Inject constructor(playerRepository: PlayerRepository) {
    private val playerWriter: suspend (player: Player, callBack:(isWrite:Boolean)->Unit) -> Unit =
        { player, callBack ->
            playerRepository.writePlayer(player, callBack)
        }

    suspend operator fun invoke(player: Player, callBack: (isWrite: Boolean) -> Unit) =
        withContext(Dispatchers.IO) {
            playerWriter(player, callBack)
        }
}