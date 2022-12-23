package com.ranseo.yatchgame.data.repo

import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.source.EmojiInfoDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmojiInfoRepository @Inject constructor(private val emojiInfoDataSource: EmojiInfoDataSource) {
    suspend fun getEmojiInfo(gameId:String, playerId:String) : Flow<Result<EmojiInfo>> = withContext(Dispatchers.IO) {
        emojiInfoDataSource.getEmojiInfo(gameId, playerId)
    }

    suspend fun writeEmojiInfo(gameId: String, playerId:String, emojiInfo: EmojiInfo) = withContext(Dispatchers.IO) {
        emojiInfoDataSource.setEmojiInfo(gameId, playerId, emojiInfo)
    }
}