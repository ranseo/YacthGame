package com.ranseo.yatchgame.data.model.gameinfo

import com.ranseo.yatchgame.data.model.Player

data class MyGameScoreAndPlayer(
    val gameResult: String,
    val firstPlayer: Player?,
    val secondPlayer: Player?
) {
}