package com.ranseo.yatchgame.domain.usecase

import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.Rematch
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import com.ranseo.yatchgame.data.repo.PlayerRepository
import com.ranseo.yatchgame.data.repo.RematchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlowRematchUseCase @Inject constructor(
    rematchRepository: RematchRepository,
    playerRepository: PlayerRepository
) {

    private val player = playerRepository.player.value
    private val flowRematchGetter: suspend (uid: String) -> Flow<Result<Rematch>> = { uid ->
        rematchRepository.getRematch(uid)
    }

    suspend operator fun invoke(): Flow<Result<Rematch>> = withContext(Dispatchers.IO) {
        flowRematchGetter(player?.playerId ?: "")
    }
}