package com.ranseo.yatchgame.data.model

data class EmojiInfo(
    val firstEmoji : Int,
    val secondEmoji : Int
) {

    constructor(hashMap:HashMap<*,*>) : this(
        firstEmoji = hashMap["firstEmoji"] as Int,
        secondEmoji = hashMap["secondEmoji"] as Int
    )

}

