package com.ranseo.yatchgame.data.model.statis

import com.ranseo.yatchgame.data.model.Board
import com.ranseo.yatchgame.data.model.Player

data class BestScore(
    val bestScore: Int,
    val boards: List<Board>?,
    val firstPlayer: Player?,
    val secondPlayer:Player?
    ) {
}