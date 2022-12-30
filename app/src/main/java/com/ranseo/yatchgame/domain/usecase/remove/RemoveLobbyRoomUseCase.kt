package com.ranseo.yatchgame.domain.usecase.remove

import com.ranseo.yatchgame.data.repo.LobbyRoomRepository
import com.ranseo.yatchgame.data.repo.PlayerRepository
import com.ranseo.yatchgame.data.repo.RematchRepository
import javax.inject.Inject

class RemoveLobbyRoomUseCase @Inject constructor(lobbyRoomRepository: LobbyRoomRepository) {

    private val lobbyRoomRemover : suspend (roomKey:String) -> Unit = { roomKey ->
        lobbyRoomRepository.removeLobbyRoomValue(roomKey)
    }

    suspend  operator fun invoke(roomKey: String) {
        lobbyRoomRemover(roomKey)
    }
}