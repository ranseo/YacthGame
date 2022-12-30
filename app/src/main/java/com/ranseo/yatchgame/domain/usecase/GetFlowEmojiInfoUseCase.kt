package com.ranseo.yatchgame.domain.usecase

import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.repo.EmojiInfoRepository
import com.ranseo.yatchgame.data.repo.GameInfoRepository
import com.ranseo.yatchgame.data.repo.PlayerRepository
import com.ranseo.yatchgame.data.repo.RollDiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlowEmojiInfoUseCase @Inject constructor(emojiInfoRepository: EmojiInfoRepository, gameInfoRepository: GameInfoRepository, playerRepository: PlayerRepository){
    private val getGameId: suspend () -> String = {
        gameInfoRepository.getGameId()
    }

    private val getPlayerId: suspend () -> String = {
       playerRepository.player.value?.playerId ?: "UNKNOWN"
    }

    private val flowEmojiInfoGetter : suspend (gameId:String, playerId:String) -> Flow<Result<EmojiInfo>> = { gameId, playerId ->
        emojiInfoRepository.getEmojiInfo(gameId, playerId)
    }

    suspend operator fun invoke() : Flow<Result<EmojiInfo>> = withContext(Dispatchers.IO) {
        flowEmojiInfoGetter(getGameId(),getPlayerId())
    }
}