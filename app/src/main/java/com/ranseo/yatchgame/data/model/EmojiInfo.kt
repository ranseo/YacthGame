package com.ranseo.yatchgame.data.model

data class EmojiInfo(
    val emoji:Int
) {

    constructor(hashMap:HashMap<*,*>) : this(
        emoji = (hashMap["emoji"] as Long).toInt(),
    )

}

