package com.ranseo.yatchgame.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class BoardTag {
    ONES,
    TWOS,
    THREES,
    FOURS,
    FIVES,
    SIXES,
    CHOICE,
    FOURCARD,
    FULLHOUSE,
    SMALLSTRAIGHT,
    LARGESTRAIGHT,
    YACHT
}

@JsonClass(generateAdapter = true)
data class Board(
    @field:Json(name = "ones")
    var ones: Int = -1,
    @field:Json(name = "twos")
    var twos: Int = -1,
    @field:Json(name = "threes")
    var threes: Int = -1,
    @field:Json(name = "fours")
    var fours: Int = -1,
    @field:Json(name = "fives")
    var fives: Int = -1,
    @field:Json(name = "sixes")
    var sixes: Int = -1,
    @field:Json(name = "sum")
    var sum: Int = 0,
    @field:Json(name = "bonus")
    var bonus: Int = -1,
    @field:Json(name = "choice")
    var choice: Int = -1,
    @field:Json(name = "fourCard")
    var fourCard: Int = -1,
    @field:Json(name = "fullHouse")
    var fullHouse: Int = -1,
    @field:Json(name = "smallStraight")
    var smallStraight: Int = -1,
    @field:Json(name = "largeStraight")
    var largeStraight: Int = -1,
    @field:Json(name = "yacht")
    var yacht: Int = -1,
    @field:Json(name = "total")
    var total: Int = 0
) {

    constructor(values: IntArray) : this(
        values[0],
        values[1],
        values[2],
        values[3],
        values[4],
        values[5],
        values[6],
        values[7],
        values[8],
        values[9],
        values[10],
        values[11],
        values[12],
        values[13],
        values[14]
    )

    constructor(hashMap: HashMap<*, *>) : this(
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

    fun plusScore(board: Board): Board {
        val ones = this.ones + board.ones
        val twos = this.twos + board.twos
        val threes = this.threes + board.threes
        val fours = this.fours + board.fours
        val fives = this.fives + board.fives
        val sixes = this.sixes + board.sixes
        val sum = ones + twos + threes + fours + fives + sixes
        val bonus = if (sum < 63) 0 else 35

        val choice = this.choice + board.choice
        val fourCard = this.fourCard + board.fourCard
        val fullHouse = this.fullHouse + board.fullHouse
        val smallStraight = this.smallStraight + board.smallStraight
        val largeStraight = this.largeStraight + board.largeStraight
        val yacht = this.yacht + board.yacht
        val total =
            sum + bonus + (choice + fourCard + fullHouse + smallStraight + largeStraight + yacht)
        return Board(
            ones,
            twos,
            threes,
            fours,
            fives,
            sixes,
            sum,
            bonus,
            choice,
            fourCard,
            fullHouse,
            smallStraight,
            largeStraight,
            yacht,
            total
        )
    }
}

data class BoardRecord(
    @field:Json(name = "isOnes")
    var isOnes: Boolean = false,
    @field:Json(name = "isTwos")
    var isTwos: Boolean = false,
    @field:Json(name = "isThrees")
    var isThrees: Boolean = false,
    @field:Json(name = "isFours")
    var isFours: Boolean = false,
    @field:Json(name = "isFives")
    var isFives: Boolean = false,
    @field:Json(name = "isSixes")
    var isSixes: Boolean = false,

    @field:Json(name = "isChoice")
    var isChoice: Boolean = false,
    @field:Json(name = "isFourCard")
    var isFourCard: Boolean = false,
    @field:Json(name = "isFullHouse")
    var isFullHouse: Boolean = false,
    @field:Json(name = "isSmallStraight")
    var isSmallStraight: Boolean = false,
    @field:Json(name = "isLargeStraight")
    var isLargeStraight: Boolean = false,
    @field:Json(name = "isYacht")
    var isYacht: Boolean = false,

    ) {

    constructor(records: Array<Boolean>) : this(
        records[0],
        records[1],
        records[2],
        records[3],
        records[4],
        records[5],
        records[6],
        records[7],
        records[8],
        records[9],
        records[10],
        records[11]
    )

    constructor(hashMap: HashMap<*, *>) : this (
        hashMap["isOnes"] as Boolean,
        hashMap["isTwos"] as Boolean,
        hashMap["isThrees"] as Boolean,
        hashMap["isFours"] as Boolean,
        hashMap["isFives"] as Boolean,
        hashMap["isSixes"] as Boolean,
        hashMap["isChoice"] as Boolean,
        hashMap["isFourCard"] as Boolean,
        hashMap["isFullHouse"] as Boolean,
        hashMap["isSmallStraight"] as Boolean,
        hashMap["isLargeStraight"] as Boolean,
        hashMap["isYacht"] as Boolean
    )
}

class OnBoardClickListener(val onClickListener: (tag: BoardTag) -> Unit) {
    fun onClick(tag: BoardTag) {
        onClickListener(tag)
    }
}
