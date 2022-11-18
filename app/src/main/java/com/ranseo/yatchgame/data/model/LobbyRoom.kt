package com.ranseo.yatchgame.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class LobbyRoom(
    val roomId : Int,
    val host: Player,
    val guest: Player,
    val condition:Boolean
)


