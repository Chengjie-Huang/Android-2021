package com.example.mapdemo.data

import android.graphics.Bitmap
import androidx.room.*

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "image")
data class ImageData(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "uri") val imageUri: String,
    @ColumnInfo(name = "title") var imageTitle: String,
    @ColumnInfo(name = "date") var imageDate: String? = null,
    @ColumnInfo(name = "longitude") var imageLongitude: Double? = null,
    @ColumnInfo(name = "latitude") var imageLatitude: Double? = null,
    @ColumnInfo(name = "description") var imageDescription: String? = null)
{
    @Ignore
    var thumbnail: Bitmap? = null
}
