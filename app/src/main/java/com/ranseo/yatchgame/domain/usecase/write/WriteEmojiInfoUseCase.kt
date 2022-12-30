package com.ranseo.yatchgame.domain.usecase.write

import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.repo.EmojiInfoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WriteEmojiInfoUseCase @Inject constructor(
    emojiInfoRepository: EmojiInfoRepository,
) {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val emojiInfoWriter: suspend (gameId:String, playerId:String ,emojiInfo: EmojiInfo) -> Unit =
        { gameId, playerId, emojiInfo ->
            emojiInfoRepository.writeEmojiInfo(gameId, playerId, emojiInfo)
        }

    suspend operator fun invoke(gameId:String, playerId:String ,emojiInfo: EmojiInfo) =
        withContext(defaultDispatcher) {
            emojiInfoWriter(gameId,playerId,emojiInfo)
        }
}