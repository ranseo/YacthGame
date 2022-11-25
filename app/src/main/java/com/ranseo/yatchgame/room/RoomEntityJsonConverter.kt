package com.ranseo.yatchgame.room

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.ranseo.yatchgame.data.model.Board
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.model.Player
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

//@ProvidedTypeConverter
//class GameInfoConverter @Inject constructor(private val moshi: Moshi) {
//    @TypeConverter
//    fun fromString(value:String) : GameInfo? {
//        val adapter : JsonAdapter<GameInfo> = moshi.adapter(GameInfo::class.java)
//        return adapter.fromJson(value)
//    }
//
//    @TypeConverter
//    fun fromGameInfo(gameInfo:GameInfo) : String {
//        val adapter : JsonAdapter<GameInfo> = moshi.adapter(GameInfo::class.java)
//        return adapter.toJson(gameInfo)
//    }
//}
//
//
@ProvidedTypeConverter
class PlayerConverter @Inject constructor(private val moshi: Moshi) {
    @TypeConverter
    fun fromString(value:String) : Player? {
        val adapter : JsonAdapter<Player> = moshi.adapter(Player::class.java)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromGameInfo(player:Player) : String {
        val adapter : JsonAdapter<Player> = moshi.adapter(Player::class.java)
        return adapter.toJson(player)
    }
}

//@ProvidedTypeConverter
//class BoardConverter @Inject constructor(private val moshi: Moshi) {
//    @TypeConverter
//    fun fromString(value:String) : Board? {
//        val adapter : JsonAdapter<Board> = moshi.adapter(Board::class.java)
//        return adapter.fromJson(value)
//    }
//
//    @TypeConverter
//    fun fromGameInfo(board:Board) : String {
//        val adapter : JsonAdapter<Board> = moshi.adapter(Board::class.java)
//        return adapter.toJson(board)
//    }
//}

@ProvidedTypeConverter
class BoardListConverter @Inject constructor(private val moshi:Moshi) {
    @TypeConverter
    fun fromString(value:String) : List<Board?>? {
        val listType = Types.newParameterizedType(List::class.java, Board::class.java)
        val adapter : JsonAdapter<List<Board>> = moshi.adapter(listType)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromBoards(boards:List<Board>) : String {
        val listType = Types.newParameterizedType(List::class.java, Board::class.java)
        val adapter : JsonAdapter<List<Board>> = moshi.adapter(listType)
        return adapter.toJson(boards)
    }

}
