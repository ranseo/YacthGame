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

    fun plusScore(board: Board, boardTag: BoardTag): Board {
        var ones = if (this.ones > -1) this.ones else 0; var twos = if (this.twos > -1) this.twos else 0;
        var threes = if (this.threes > -1) this.threes else 0; var fours = if (this.fours > -1) this.fours else 0;
        var fives = if(this.fives >-1) this.fives else 0; var sixes = if(this.sixes >-1) this.sixes else 0;
        var choice = if(this.choice >-1) this.choice else 0; var fourCard = if(this.fourCard >-1) this.fourCard else 0;
        var fullHouse = if(this.fullHouse >-1) this.fullHouse else 0; var smallStraight = if(this.smallStraight >-1) this.smallStraight else 0;
        var largeStraight = if(this.largeStraight >-1) this.largeStraight else 0; var yacht = if(this.yacht >-1) this.yacht else 0;

        when (boardTag) {
            BoardTag.ONES -> ones += board.ones
            BoardTag.TWOS -> twos += board.twos
            BoardTag.THREES -> threes += board.threes
            BoardTag.FOURS -> fours += board.fours
            BoardTag.FIVES -> fives += board.fives
            BoardTag.SIXES -> sixes += board.sixes
            BoardTag.CHOICE -> choice += board.choice
            BoardTag.FOURCARD -> fourCard += board.fourCard
            BoardTag.FULLHOUSE -> fullHouse += board.fullHouse
            BoardTag.SMALLSTRAIGHT -> smallStraight += board.smallStraight
            BoardTag.LARGESTRAIGHT -> largeStraight += board.largeStraight
            BoardTag.YACHT -> yacht += board.yacht
        }


        this.sum = ones + twos + threes + fours + fives + sixes
        this.bonus = if (sum < 63) 0 else 35

        this.total =
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

    constructor(hashMap: HashMap<*, *>) : this(
        hashMap["ones"] as Boolean,
        hashMap["twos"] as Boolean,
        hashMap["threes"] as Boolean,
        hashMap["fours"] as Boolean,
        hashMap["fives"] as Boolean,
        hashMap["sixes"] as Boolean,
        hashMap["choice"] as Boolean,
        hashMap["fourCard"] as Boolean,
        hashMap["fullHouse"] as Boolean,
        hashMap["smallStraight"] as Boolean,
        hashMap["largeStraight"] as Boolean,
        hashMap["yacht"] as Boolean
    )
}

class OnBoardClickListener(val onClickListener: (tag: BoardTag) -> Unit) {
    fun onClick(tag: BoardTag) {
        onClickListener(tag)
    }
}
