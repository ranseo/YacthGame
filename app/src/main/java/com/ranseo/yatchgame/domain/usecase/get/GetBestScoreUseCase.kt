package com.ranseo.yatchgame.domain.usecase.get

import androidx.lifecycle.LiveData
import com.ranseo.yatchgame.data.model.BoardInfo
import com.ranseo.yatchgame.data.model.EmojiInfo
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.model.statis.BestScore
import com.ranseo.yatchgame.data.model.statis.WinDrawLose
import com.ranseo.yatchgame.data.repo.BoardInfoRepository
import com.ranseo.yatchgame.data.repo.GameInfoRepository
import com.ranseo.yatchgame.data.repo.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetBestScoreUseCase @Inject constructor(gameInfoRepository: GameInfoRepository){

    private val bestScoreGetter : (player:Player)->LiveData<BestScore> = { player ->
        gameInfoRepository.getBestScore(player)
    }

    operator fun invoke(player:Player) : LiveData<BestScore> =  bestScoreGetter(player)

}