package com.ranseo.yatchgame.data.model

data class WaitingRoom(
    val roomId: String,
    val host: MutableMap<String, Player>,
    val guest: MutableMap<String, Player>?
) {
    constructor(hashMap: HashMap<*, *>) : this(
        hashMap["roomId"] as String,
        hashMap["host"] as MutableMap<String, Player>,
        hashMap["guest"] as MutableMap<String, Player>?
    )

    constructor(waitingRoom: WaitingRoom, guest: MutableMap<String, Player>) : this(
        waitingRoom.roomId,
        waitingRoom.host,
        guest
    )

    fun getHostPlayer() : Player? {
        return this.host.getOrDefault("host", null)
    }

    fun getGuestPlayer() : Player? {
        return if(this.guest==null) null else this.guest.getOrDefault("guest", null)
    }

}