package com.ranseo.yatchgame.data.model

data class TurnCountInfo(
    val turnCount:Int
) {
    constructor(turnCount: Long) : this(
        turnCount = turnCount.toInt()
    )
}