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
    var sixes:Int=0,
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
        (hashMap["ones"] as Long).toInt(),
        (hashMap["twos"] as Long).toInt(),
        (hashMap["threes"] as Long).toInt(),
        (hashMap["fours"] as Long).toInt(),
        (hashMap["fives"] as Long).toInt(),
        (hashMap["sixes"] as Long).toInt(),
        (hashMap["sum"] as Long).toInt(),
        (hashMap["bonus"] as Long).toInt(),
        (hashMap["choice"] as Long).toInt(),
        (hashMap["fourCard"] as Long).toInt(),
        (hashMap["fullHouse"] as Long).toInt(),
        (hashMap["smallStraight"] as Long).toInt(),
        (hashMap["largeStraight"] as Long).toInt(),
        (hashMap["yacht"] as Long).toInt(),
        (hashMap["total"] as Long).toInt()
    )


}
