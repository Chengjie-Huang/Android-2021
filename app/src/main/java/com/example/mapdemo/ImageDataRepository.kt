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
                        //Log.i("MyRepository", "number generated: " + param.number.toString()
                        //        + ", inserted with id: " + insertedId.toString() + "")
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
    fun getData(): LiveData<List<ImageData>?>? {
        return mDBDao?.getItems()
    }

    /**
     * called by the UI to insert a new ImageData
     */
    suspend fun insertNewImageData(imageData: ImageData) {
        InsertAsyncTask(mDBDao).insertInBackground(imageData)
    }
}