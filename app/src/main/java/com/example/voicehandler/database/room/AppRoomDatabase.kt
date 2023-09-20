package com.example.voicehandler.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.voicehandler.database.room.dao.NoteRoomDao
import com.example.voicehandler.model.Note
import com.example.voicehandler.utils.Constants.Keys.NOTE_DATABASE


@Database(entities = [Note::class], version = 1)
abstract class AppRoomDatabase : RoomDatabase(){
    abstract fun getRoomDao(): NoteRoomDao

    companion object{

        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun genInstance(context: Context) :AppRoomDatabase{
            return if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppRoomDatabase::class.java,
                    NOTE_DATABASE
                ).build()
                INSTANCE as AppRoomDatabase
            } else INSTANCE as AppRoomDatabase
        }
    }
}