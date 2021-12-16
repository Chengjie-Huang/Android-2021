package com.example.mapdemo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.runBlocking

/**
 * Database class with a singleton INSTANCE object.
 */
@Database(entities = [ImageData::class], version = 3, exportSchema = false)
abstract class ImageRoomDatabase: RoomDatabase() {
    abstract fun imageDataDao(): ImageDataDao

    companion object {
        @Volatile
        private var INSTANCE: ImageRoomDatabase? = null
        private var mutex = Mutex()

        fun getDatabase(context: Context): ImageRoomDatabase? {
            if (INSTANCE == null) {
                runBlocking {
                    withContext(Dispatchers.Default) {
                        mutex.withLock(ImageRoomDatabase::class) {
                            INSTANCE = databaseBuilder(
                                context.applicationContext,
                                ImageRoomDatabase::class.java,
                                "image_database"
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build()
                        }
                    }
                }
            }
            return INSTANCE
        }

        private val sRoomDatabaseCallback: RoomDatabase.Callback = object : Callback() {
            override fun onOpen(db : SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }
    }
}
