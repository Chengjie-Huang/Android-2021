package com.example.mapdemo.data

import android.app.Application
import kotlinx.coroutines.*
import pl.aprilapps.easyphotopicker.MediaFile

class ImageDataUtils : Application() {
    private lateinit var daoObj: ImageDataDao
    private val databaseObj: ImageRoomDatabase by lazy { ImageRoomDatabase.getDatabase(this) }

    /**
     * Init dao by loading from the database
     */
    @DelicateCoroutinesApi
    private fun initData() {
        GlobalScope.launch {
            daoObj = databaseObj.imageDataDao()
        }
    }

    /**
     * insert a ImageData into the database
     * Called for each image the user adds by clicking the fab button
     * Then retrieves the same image so we can have the automatically assigned id field
     */
    private fun insertData(imageData: ImageData): Int = runBlocking {
        val insertJob = async { daoObj.insert(imageData) }
        insertJob.await().toInt()
    }

    /**
     * given a list of photos, it creates a list of ImageData objects
     * we do not know how many elements we will have
     * @param photos
     * @return
     */
    private fun getImageData(photos: Array<MediaFile>): List<ImageData> {
        val imageDataList: MutableList<ImageData> = ArrayList<ImageData>()
        for (mediaFile in photos) {
            val fileName = mediaFile.file.name
            var imageData = ImageData(imageTitle = fileName, imageUri = mediaFile.file.absolutePath)
            var id = insertData(imageData)
            imageData.id = id
            imageDataList.add(imageData)
        }
        return imageDataList
    }

}