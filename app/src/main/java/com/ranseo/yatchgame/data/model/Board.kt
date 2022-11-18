package com.ranseo.yatchgame.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter=true)
data class Board(
    @field:Json(name="boardId")
    val boardId: Int,
    @field:Json(name="ones")
    var ones:Int,
    @field:Json(name="twos")
    var twos:Int,
    @field:Json(name="threes")
    var threes:Int,
    @field:Json(name="fours")
    var fours:Int,
    @field:Json(name="fives")
    var fives:Int,
    @field:Json(name="sixs")
    var sixs:Int,
    @field:Json(name="sum")
    var sum:Int,
    @field:Json(name="bonus")
    var bonus:Int,
    @field:Json(name="choice")
    var choice:Int,
    @field:Json(name="fourCard")
    var fourCard:Int,
    @field:Json(name="fullHouse")
    var fullHouse:Int,
    @field:Json(name="smallStraight")
    var smallStraight:Int,
    @field:Json(name="largeStraight")
    var largeStraight:Int,
    @field:Json(name="yacht")
    var yacht:Int,
    @field:Json(name="total")
    var total:Int
)
