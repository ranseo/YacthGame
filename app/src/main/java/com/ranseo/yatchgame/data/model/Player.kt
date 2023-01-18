package com.ranseo.yatchgame.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "player_table")
data class Player (
    @field:Json(name = "playerId")
    @PrimaryKey(autoGenerate = false)
    val playerId: String,
    @field:Json(name = "name")
    val name: String
    //val statics: List<Static>
) {
    constructor(hashMap: HashMap<*, *>) : this(
        hashMap["playerId"] as String,
        hashMap["name"] as String
    )


    companion object {
        fun getEmptyPlayer(): Player {
            return Player("null", "null")
        }
    }

    override fun equals(other: Any?): Boolean {
        return if(other==null) false
        else {
            this.playerId == (other as Player).playerId
        }
    }
}




