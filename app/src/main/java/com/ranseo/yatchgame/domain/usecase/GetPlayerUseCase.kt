package com.ranseo.yatchgame.domain.usecase

import androidx.lifecycle.LiveData
import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import com.ranseo.yatchgame.data.repo.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetPlayerUseCase @Inject constructor(playerRepository: PlayerRepository){
    private val player : LiveData<Player> = playerRepository.player

    operator fun invoke() : LiveData<Player> = player
}