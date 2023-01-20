package com.ranseo.yatchgame.domain.usecase.get

import androidx.lifecycle.LiveData
import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.model.statis.WinDrawLose
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import com.ranseo.yatchgame.data.repo.GameInfoRepository
import com.ranseo.yatchgame.data.repo.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetWinDrawLoseUseCase @Inject constructor(gameInfoRepository: GameInfoRepository){

    private val winDrawLoseGetter : (player:Player)->LiveData<WinDrawLose> = { player ->
        gameInfoRepository.getWinDrawLose(player)
    }

    operator fun invoke(player:Player) : LiveData<WinDrawLose> =  winDrawLoseGetter(player)

}