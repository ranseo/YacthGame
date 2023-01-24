package com.ranseo.yatchgame.data.model.gameinfo

import com.ranseo.yatchgame.data.model.Board
import com.ranseo.yatchgame.data.model.Player

data class MyGameScoreAndPlayer(
    val gameResult: String,
    val gameScore:String,
    val firstPlayer: Player,
    val secondPlayer: Player,
    val boards:List<Board>
) {
}