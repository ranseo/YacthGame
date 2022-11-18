package com.ranseo.yatchgame.di

import android.content.Context
import androidx.room.Room
import com.ranseo.yatchgame.room.*
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDatabaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext context: Context,
        playerConverter: PlayerConverter,
        boardConverter: BoardConverter
        ) : YachtRoomDatabase {
        return Room.databaseBuilder(
            context,
            YachtRoomDatabase::class.java,
            "yacht_room_database"
        )
            .fallbackToDestructiveMigration()
            .addTypeConverter(playerConverter)
            .addTypeConverter(boardConverter)
            .build()
    }

    @Provides
    fun provideYachtDao(database:YachtRoomDatabase) : YachtRoomDao = database.yacthDao()

//    @Provides
//    @Singleton
//    fun provideGameInfoConverter(moshi: Moshi) = GameInfoConverter(moshi)
//
    @Provides
    @Singleton
    fun providePlayerConverter(moshi: Moshi) = PlayerConverter(moshi)

    @Provides
    @Singleton
    fun provideBoardConverter(moshi: Moshi) = BoardConverter(moshi)

}