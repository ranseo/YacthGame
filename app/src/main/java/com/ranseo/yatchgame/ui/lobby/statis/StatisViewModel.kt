package com.ranseo.yatchgame.ui.lobby.statis

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.ranseo.yatchgame.R
import com.ranseo.yatchgame.data.model.Board
import com.ranseo.yatchgame.data.model.BoardRecord
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.model.Player
import com.ranseo.yatchgame.data.model.statis.BestScore
import com.ranseo.yatchgame.data.model.statis.WinDrawLose
import com.ranseo.yatchgame.domain.usecase.get.GetBestScoreUseCase
import com.ranseo.yatchgame.domain.usecase.get.GetGameInfosUseCase
import com.ranseo.yatchgame.domain.usecase.get.GetPlayerUseCase
import com.ranseo.yatchgame.domain.usecase.get.GetWinDrawLoseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisViewModel @Inject constructor(
    getPlayerUseCase: GetPlayerUseCase,
    private val getWinDrawLoseUseCase: GetWinDrawLoseUseCase,
    private val getBestScoreUseCase: GetBestScoreUseCase,
    application: Application
) : AndroidViewModel(application) {

    val player : LiveData<Player> = getPlayerUseCase()

    lateinit var winDrawLose : LiveData<WinDrawLose>
    lateinit var winDrawLoseStr : LiveData<String>
    lateinit var winRate : LiveData<String>

    lateinit var bestScore : LiveData<BestScore>
    lateinit var bestScoreStr : LiveData<String>
    lateinit var bestFirstBoard : LiveData<Board?>
    lateinit var bestSecondBoard : LiveData<Board?>


    val boardRecord : LiveData<BoardRecord> = liveData { BoardRecord(arrayOf(true,true,true,true,true,true,true,true,true,true,true,true)) }
    val emptyBoard : LiveData<Board> = liveData { Board() }
    val turnCountStr : LiveData<String> = liveData {""}
    val turnLight : LiveData<Int> = liveData { ContextCompat.getColor(getApplication(), R.color.black)}
    val
    init {
    }


    fun getWinDrawLose(player:Player) {
        winDrawLose = getWinDrawLoseUseCase(player)
        winDrawLoseStr = Transformations.map(winDrawLose) { winDrawLose ->
            "${winDrawLose.win}승 ${winDrawLose.draw}무 ${winDrawLose.lose}패"
        }
        winRate = Transformations.map(winDrawLose) { winDrawLose ->
            if(winDrawLose.win + winDrawLose.lose == 0) "0%"
            else "승률 : ${String.format("%.2f", (winDrawLose.win).toDouble()/(winDrawLose.win + winDrawLose.lose).toDouble() * 100)}%"
        }
    }

    fun getBestScore(player:Player) {

    }





}