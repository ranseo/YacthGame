package com.ranseo.yatchgame.domain.usecase.write

import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.Rematch
import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import com.ranseo.yatchgame.data.repo.EmojiInfoRepository
import com.ranseo.yatchgame.data.repo.RematchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WriteRematchUseCase @Inject constructor(
    rematchRepository: RematchRepository,
) {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val rematchWriter: suspend (rematch: Rematch) -> Flow<Result<String>> =
        { rematch ->
            rematchRepository.writeRematch(rematch)
        }

    suspend operator fun invoke(rematch: Rematch)  : Flow<Result<String>> =
        withContext(defaultDispatcher) {
            rematchWriter(rematch)
        }
}