package com.example.mapdemo

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import com.example.mapdemo.data.ImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.MediaFile

class ImageDataViewModel(application: Application) : AndroidViewModel(application) {
    private var mRepository: ImageDataRepository = ImageDataRepository(application)
    private var imageDataToDisplay: LiveData<List<ImageData>?>? = this.mRepository.getData()

    fun getImageDataToDisplay(): LiveData<List<ImageData>?>? {
        if (this.imageDataToDisplay == null) {
            this.imageDataToDisplay = MutableLiveData<List<ImageData>>()
        }

        return this.imageDataToDisplay
    }

    fun getImageDataByTitle(title: String): LiveData<List<ImageData>?>? {
        this.imageDataToDisplay = this.mRepository.getDataByTitle(title)
        if (this.imageDataToDisplay == null) {
            this.imageDataToDisplay = MutableLiveData<List<ImageData>>()
        }

        return this.imageDataToDisplay
    }

    fun getImageDataByDate(date: String): LiveData<List<ImageData>?>? {
        this.imageDataToDisplay = this.mRepository.getDataByDate(date)
        if (this.imageDataToDisplay == null) {
            this.imageDataToDisplay = MutableLiveData<List<ImageData>>()
        }

        return this.imageDataToDisplay
    }

    fun getImageDataByTitleAndDate(title: String, date: String): LiveData<List<ImageData>?>? {
        this.imageDataToDisplay = this.mRepository.getDataByTitleAndDate(title, date)
        if (this.imageDataToDisplay == null) {
            this.imageDataToDisplay = MutableLiveData<List<ImageData>>()
        }

        return this.imageDataToDisplay
    }

    fun insertNewImageData(imageData: ImageData) {
        viewModelScope.launch(Dispatchers.IO) {
            mRepository.insertNewImageData(imageData)
        }
    }

    fun deleteImageData(imageData: ImageData) {
        viewModelScope.launch(Dispatchers.IO) {
            mRepository.deleteImageData(imageData)
        }
    }

    fun updateImageData(imageData: ImageData) {
        viewModelScope.launch(Dispatchers.IO) {
            mRepository.updateImageData(imageData)
        }
    }
}