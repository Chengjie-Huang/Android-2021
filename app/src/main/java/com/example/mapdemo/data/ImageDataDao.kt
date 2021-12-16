package com.example.mapdemo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.lifecycle.LiveData

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ImageDataDao {
    @Query("SELECT * from image ORDER BY id ASC")
    fun getItems(): LiveData<List<ImageData>?>?

    @Query("SELECT * from image WHERE id = :id")
    fun getItem(id: Int): LiveData<ImageData>

    @Query("SELECT * from image ORDER BY id ASC")
    fun getItemsRaw(): List<ImageData>?

    @Query("SELECT * from image WHERE title LIKE :title")
    fun getItemsByTitle(title: String): LiveData<List<ImageData>?>?

    @Query("SELECT * from image WHERE date LIKE :date")
    fun getItemsByDate(date: String): LiveData<List<ImageData>?>?

    @Query("SELECT * from image WHERE title LIKE :title and date LIKE :date")
    fun getItemsByTitleAndDate(title: String, date: String): LiveData<List<ImageData>?>?

    // Specify the conflict strategy as REPLACE,
    // when the trying to add an existing Item
    // into the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(singleImageData: ImageData): Long

    @Update
    suspend fun update(imageData: ImageData)

    @Delete
    fun delete(imageData: ImageData?)

    @Delete
    fun deleteAll(vararg imageData: ImageData?)
}