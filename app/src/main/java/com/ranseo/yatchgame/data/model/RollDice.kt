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
    val turn: Boolean //rollDice에서 turn이 true면 first player가 false면 second player의 턴이다.
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

    fun checkDiceChange(rollDice: RollDice): Boolean {
        return (rollDice.firstFix != this.firstFix
                || rollDice.secondFix != this.secondFix
                || rollDice.thirdFix != this.thirdFix
                || rollDice.fourthFix != this.fourthFix
                || rollDice.fifthFix != this.fifthFix)
    }
}
