package com.ranseo.yatchgame.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "game_info_table")
data class GameInfo(
    @PrimaryKey(autoGenerate = false)
    @field:Json(name="gameId")
    val gameId:String,
    @field:Json(name = "gameScore")
    var gameScore : String,
    @field:Json(name="first")
    var first:Player,
    @field:Json(name="second")
    var second:Player,
    @field:Json(name = "result")
    var result: String,
    @field:Json(name="board")
    var board:Board
)


