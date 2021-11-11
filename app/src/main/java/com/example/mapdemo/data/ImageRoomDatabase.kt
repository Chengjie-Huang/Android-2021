package com.example.mapdemo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton INSTANCE object.
 */
@Database(entities = [ImageData::class], version = 2, exportSchema = false)
abstract class ImageRoomDatabase: RoomDatabase() {

    abstract fun imageDataDao(): ImageDataDao

    companion object {
        @Volatile
        private var INSTANCE: ImageRoomDatabase? = null
        fun getDatabase(context: Context): ImageRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                if (INSTANCE != null) {
                    return INSTANCE as ImageRoomDatabase
                }

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImageRoomDatabase::class.java,
                    "image_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
