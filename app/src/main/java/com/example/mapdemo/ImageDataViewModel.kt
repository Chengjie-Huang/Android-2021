package com.example.mapdemo

import android.app.Application
import androidx.lifecycle.*
import com.example.mapdemo.data.ImageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageDataViewModel(application: Application) : AndroidViewModel(application) {
    private var mRepository: ImageDataRepository = ImageDataRepository(application)
    private var imageDataToDisplay: LiveData<List<ImageData>?>? = this.mRepository.getData()

    fun getImageDataToDisplay(): LiveData<List<ImageData>?>? {
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
}