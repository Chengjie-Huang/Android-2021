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
    @ColumnInfo(name = "longitude") var imageLongitude: Float? = null,
    @ColumnInfo(name = "altitude") var imageAltitude: Float? = null,
    @ColumnInfo(name = "description") var imageDescription: String? = null,
    @ColumnInfo(name = "thumbnailUri") var thumbnailUri: String? = null)
{
    @Ignore
    var thumbnail: Bitmap? = null
}
