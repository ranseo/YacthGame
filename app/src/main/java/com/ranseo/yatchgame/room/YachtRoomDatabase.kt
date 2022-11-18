package com.ranseo.yatchgame.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ranseo.yatchgame.data.model.GameInfo
import com.ranseo.yatchgame.data.model.Player

@Database(entities = [Player::class, GameInfo::class], version = 1, exportSchema = false)
@TypeConverters(value = [PlayerConverter::class, BoardConverter::class])
abstract class YachtRoomDatabase : RoomDatabase() {
    abstract fun yacthDao() : YachtRoomDao
}