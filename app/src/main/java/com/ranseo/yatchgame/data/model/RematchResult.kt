package com.ranseo.yatchgame.data.model

data class RematchResult(
    val rematchId: String,
    val isAccept: Boolean,
) {
    constructor(hashMap: HashMap<*,*>) : this(
        rematchId = hashMap["rematchId"] as String,
        isAccept = hashMap["isAccept"] as Boolean,
    )
}
