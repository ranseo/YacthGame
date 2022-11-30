package com.ranseo.yatchgame.data.model

data class RollDice(
    val gameId: String,
    val first: Int,
    val second: Int,
    val third: Int,
    val fourth: Int,
    val fifth: Int,
    val firstFix: Boolean,
    val secondFix: Boolean,
    val thirdFix: Boolean,
    val fourthFix: Boolean,
    val fifthFix: Boolean,
    val turn: Boolean
) {
    constructor(gameId: String, dices: Array<Int>, keeps: Array<Boolean>, turn: Boolean) : this(
        gameId,
        dices[0],
        dices[1],
        dices[2],
        dices[3],
        dices[4],
        keeps[0],
        keeps[1],
        keeps[2],
        keeps[3],
        keeps[4],
        turn
    )

    constructor(hashMap: HashMap<*, *>) : this(
        hashMap["gameId"] as String,
        (hashMap["first"] as Long).toInt(),
        (hashMap["second"] as Long).toInt(),
        (hashMap["third"] as Long).toInt(),
        (hashMap["fourth"] as Long).toInt(),
        (hashMap["fifth"] as Long).toInt(),
        hashMap["firstFix"] as Boolean,
        hashMap["secondFix"] as Boolean,
        hashMap["thirdFix"] as Boolean,
        hashMap["fourthFix"] as Boolean,
        hashMap["fifthFix"] as Boolean,
        hashMap["turn"] as Boolean,
    )
}
