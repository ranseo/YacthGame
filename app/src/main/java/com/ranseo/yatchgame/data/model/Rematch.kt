package com.ranseo.yatchgame.data.model

data class Rematch(
    val rematchId: String,
    val rematch: Boolean,
    val requestPlayer: Player,
    val message: String
) {
    constructor(rematchRemote: RematchRemote) : this(
        rematchId = rematchRemote.rematchId,
        rematch = rematchRemote.rematch,
        requestPlayer = rematchRemote.requestPlayer["requestPlayer"] as Player,
        message = rematchRemote.message
    )
}

data class RematchRemote(
    val rematchId: String,
    val rematch: Boolean,
    val requestPlayer: MutableMap<String, Player>,
    val message: String
) {
    constructor(rematch: Rematch) : this(
        rematchId = rematch.rematchId,
        rematch = rematch.rematch,
        requestPlayer = mutableMapOf("requestPlayer" to rematch.requestPlayer),
        message = rematch.message
    )

    constructor(hashMap: HashMap<*,*>, player: Player) : this(
        rematchId = hashMap["rematchId"] as String,
        rematch = hashMap["rematch"] as Boolean,
        requestPlayer = mutableMapOf("requestPlayer" to player),
        message = hashMap["message"] as String

    )
}

fun Rematch.asRemoteModel() = RematchRemote(this)
fun RematchRemote.asUIState() = Rematch(this)