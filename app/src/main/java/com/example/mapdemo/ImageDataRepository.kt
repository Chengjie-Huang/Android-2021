package com.example.mapdemo

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mapdemo.data.ImageDataDao
import com.example.mapdemo.data.ImageData
import com.example.mapdemo.data.ImageRoomDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ImageDataRepository(application: Application) {
    private var mDBDao: ImageDataDao? = null

    init {
        val db: ImageRoomDatabase? = ImageRoomDatabase.getDatabase(application)
        if (db != null) {
            mDBDao = db.imageDataDao()
        }
    }

    companion object {
        private val scope = CoroutineScope(Dispatchers.IO)
        private class InsertAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
            suspend fun insertInBackground(vararg params: ImageData) {
                scope.launch {
                    for(param in params){
                        val insertedId: Int? = this@InsertAsyncTask.dao?.insert(param)?.toInt()
                        // you may want to check if insertedId is null to confirm successful insertion
                        Log.i("MyRepository", "image uri: " + param.imageUri
                                + ", inserted with id: " + insertedId.toString() + "")
                    }
                }
            }
        }

        private class DeleteAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
            suspend fun deleteInBackground(vararg params: ImageData) {
                scope.launch {
                    for(param in params){
                        this@DeleteAsyncTask.dao?.delete(param)
                    }
                }
            }
        }

        private class UpdateAsyncTask(private val dao: ImageDataDao?) : ViewModel() {
            suspend fun updateInBackground(vararg params: ImageData) {
                scope.launch {
                    for(param in params){
                        this@UpdateAsyncTask.dao?.update(param)
                    }
                }
            }
        }
    }

    /**
     * it gets the data when changed in the db and returns it to the ViewModel
     * @return
     */
    fun getDataByTitle(title: String): LiveData<List<ImageData>?>? {
        return mDBDao?.getItemsByTitle(title)
    }

    /**
     * it gets the data when changed in the db and returns it to the ViewModel
     * @return
     */
    fun getDataByDate(date: String): LiveData<List<ImageData>?>? {
        return mDBDao?.getItemsByDate(date)
    }

    /**
     * it gets the data when changed in the db and returns it to the ViewModel
     * @return
     */
    fun getDataByTitleAndDate(title: String, date: String): LiveData<List<ImageData>?>? {
        return mDBDao?.getItemsByTitleAndDate(title, date)
    }

    /**
     * it gets the data when changed in the db and returns it to the ViewModel
     * @return
     */
    fun getData(): LiveData<List<ImageData>?>? {
        return mDBDao?.getItems()
    }

    /**
     * it gets the data when changed in the db and returns it to the ViewModel
     * @return
     */
    fun getDataRaw(): List<ImageData>? {
        return mDBDao?.getItemsRaw()
    }

    /**
     * called by the UI to insert a new ImageData
     */
    suspend fun insertNewImageData(imageData: ImageData) {
        InsertAsyncTask(mDBDao).insertInBackground(imageData)
    }

    /**
     * called by the UI to delete a ImageData
     */
    suspend fun deleteImageData(imageData: ImageData) {
        DeleteAsyncTask(mDBDao).deleteInBackground(imageData)
    }

    /**
     * called by the UI to update a ImageData
     */
    suspend fun updateImageData(imageData: ImageData) {
        UpdateAsyncTask(mDBDao).updateInBackground(imageData)
    }
}