package com.ranseo.yatchgame.data.model

data class Rematch(
    val rematchId: String,
    val rematch: Boolean,
    val requestPlayer: Player,
    val message: String,
    val newGameKey:String = ""
) {
    constructor(rematchRemote: RematchRemote) : this(
        rematchId = rematchRemote.rematchId,
        rematch = rematchRemote.rematch,
        requestPlayer = rematchRemote.requestPlayer["requestPlayer"] as Player,
        message = rematchRemote.message,
        newGameKey = rematchRemote.newGameKey
    )
}

data class RematchRemote(
    val rematchId: String,
    val rematch: Boolean,
    val requestPlayer: MutableMap<String, Player>,
    val message: String,
    val newGameKey: String
) {
    constructor(rematch: Rematch, newGameKey: String) : this(
        rematchId = rematch.rematchId,
        rematch = rematch.rematch,
        requestPlayer = mutableMapOf("requestPlayer" to rematch.requestPlayer),
        message = rematch.message,
        newGameKey = newGameKey
    )

    constructor(hashMap: HashMap<*,*>, player: Player) : this(
        rematchId = hashMap["rematchId"] as String,
        rematch = hashMap["rematch"] as Boolean,
        requestPlayer = mutableMapOf("requestPlayer" to player),
        message = hashMap["message"] as String,
        newGameKey = hashMap["newGameKey"] as String

    )
}

fun Rematch.asRemoteModel(newGameKey:String) = RematchRemote(this, newGameKey)
fun RematchRemote.asUIState() = Rematch(this)