package com.ranseo.yatchgame.domain.usecase.write

import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.LobbyRoom
import com.ranseo.yatchgame.data.model.RollDice
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import com.ranseo.yatchgame.data.repo.EmojiInfoRepository
import com.ranseo.yatchgame.data.repo.LobbyRoomRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WriteLobbyRoomUseCase @Inject constructor(
    lobbyRoomRepository: LobbyRoomRepository,
) {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val lobbyRoomWriter: suspend (lobbyRoom:LobbyRoom, callback: (roomKey:String) -> Unit) -> Unit =
        { lobbyRoom, callback ->
            lobbyRoomRepository.writeLobbyRoom(lobbyRoom, callback)
        }

    suspend operator fun invoke(lobbyRoom: LobbyRoom, callback: (roomKey: String) -> Unit) =
        withContext(defaultDispatcher) {
            lobbyRoomWriter(lobbyRoom, callback)
        }
}