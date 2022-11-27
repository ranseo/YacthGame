package com.ranseo.yatchgame.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter=true)
data class Board(
    @field:Json(name="ones")
    var ones:Int=0,
    @field:Json(name="twos")
    var twos:Int=0,
    @field:Json(name="threes")
    var threes:Int=0,
    @field:Json(name="fours")
    var fours:Int=0,
    @field:Json(name="fives")
    var fives:Int=0,
    @field:Json(name="sixs")
    var sixs:Int=0,
    @field:Json(name="sum")
    var sum:Int=0,
    @field:Json(name="bonus")
    var bonus:Int=0,
    @field:Json(name="choice")
    var choice:Int=0,
    @field:Json(name="fourCard")
    var fourCard:Int=0,
    @field:Json(name="fullHouse")
    var fullHouse:Int=0,
    @field:Json(name="smallStraight")
    var smallStraight:Int=0,
    @field:Json(name="largeStraight")
    var largeStraight:Int=0,
    @field:Json(name="yacht")
    var yacht:Int=0,
    @field:Json(name="total")
    var total:Int=0
) {

    constructor(hashMap:HashMap<*,*>): this(
        hashMap["ones"] as Int,
        hashMap["twos"] as Int,
        hashMap["threes"] as Int,
        hashMap["fours"] as Int,
        hashMap["fives"] as Int,
        hashMap["sixs"] as Int,
        hashMap["sum"] as Int,
        hashMap["bonus"] as Int,
        hashMap["choice"] as Int,
        hashMap["fourCard"] as Int,
        hashMap["fullHouse"] as Int,
        hashMap["smallStraight"] as Int,
        hashMap["largeStraight"] as Int,
        hashMap["yacht"] as Int,
        hashMap["total"] as Int
    )
}
