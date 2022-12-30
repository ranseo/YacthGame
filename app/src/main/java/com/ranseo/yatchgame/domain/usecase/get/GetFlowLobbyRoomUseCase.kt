package com.ranseo.yatchgame.domain.usecase.get

import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.LobbyRoom
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import com.ranseo.yatchgame.data.repo.GameInfoRepository
import com.ranseo.yatchgame.data.repo.LobbyRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFlowLobbyRoomUseCase @Inject constructor(lobbyRoomRepository: LobbyRoomRepository){

    private val flowLobbyRoomGetter : suspend () -> Flow<Result<List<LobbyRoom>>> = {
        lobbyRoomRepository.getLobbyRooms()
    }

    suspend operator fun invoke() : Flow<Result<List<LobbyRoom>>> = withContext(Dispatchers.IO) {
        flowLobbyRoomGetter()
    }
}