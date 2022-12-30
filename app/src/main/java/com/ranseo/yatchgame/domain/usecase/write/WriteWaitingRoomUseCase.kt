package com.ranseo.yatchgame.domain.usecase.write

import com.ranseo.yatchgame.data.model.*
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import com.ranseo.yatchgame.data.repo.EmojiInfoRepository
import com.ranseo.yatchgame.data.repo.LobbyRoomRepository
import com.ranseo.yatchgame.data.repo.WaitingRoomRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WriteWaitingRoomUseCase @Inject constructor(
    waitingRoomRepository: WaitingRoomRepository,
) {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val waitingRoomWriter: suspend (waitingRoom:WaitingRoom)->Unit =
        { waitingRoom ->
            waitingRoomRepository.writeWaitingRoom(waitingRoom)
        }

    suspend operator fun invoke(waitingRoom: WaitingRoom) =
        withContext(defaultDispatcher) {
            waitingRoomWriter(waitingRoom)
        }
}