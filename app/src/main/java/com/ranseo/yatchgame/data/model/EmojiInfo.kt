package com.ranseo.yatchgame.data.model

data class EmojiInfo(
    val emoji:Int,
    val flag : Boolean = true
) {

    constructor(hashMap:HashMap<*,*>) : this(
        emoji = (hashMap["emoji"] as Long).toInt(),
        flag = hashMap["flag"] as Boolean
    )

}

