package com.ranseo.yatchgame.data.model

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.common.util.DataUtils

data class LobbyRoom(
    val roomName : String,
    val host: Player?,
    val guest: Player?,
    val condition:Boolean
) {
    constructor(hashMap:HashMap<*,*>) : this(
        hashMap["roomName"]!! as String,
        hashMap["host"] as Player?,
        hashMap["guest"] as Player?,
        hashMap["condition"] as Boolean
    )

    companion object {

        private val itemCallback = object : DiffUtil.ItemCallback<LobbyRoom>() {
            override fun areItemsTheSame(oldItem: LobbyRoom, newItem: LobbyRoom): Boolean = oldItem.roomName == newItem.roomName
            override fun areContentsTheSame(oldItem: LobbyRoom, newItem: LobbyRoom): Boolean = oldItem == newItem
        }

        fun getItemCallback() = itemCallback


    }

}
