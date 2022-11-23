package com.ranseo.yatchgame.data.model

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.common.util.DataUtils

data class LobbyRoom(
    val roomId:String, //host의 uid
    val roomName : String,
    val host: MutableMap<String, Player>,
    val guest: MutableMap<String, Player>?,
    val condition:Boolean
) {
    constructor(hashMap:HashMap<*,*>) : this (
        hashMap["roomId"] as String,
        hashMap["roomName"]!! as String,
        hashMap["host"] as MutableMap<String,Player>,
        hashMap["guest"] as MutableMap<String,Player>?,
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
