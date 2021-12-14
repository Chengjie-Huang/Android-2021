package com.example.mapdemo

import android.app.Application
import com.example.mapdemo.data.ImageRoomDatabase

class ImageApplication: Application() {
    val databaseObj: ImageRoomDatabase by lazy { ImageRoomDatabase.getDatabase(this) }
}
