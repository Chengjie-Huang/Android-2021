package com.example.mapdemo

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.core.content.FileProvider
import com.google.android.gms.maps.GoogleMap
import java.io.File
import java.io.IOException

class SecondVisitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.visit_second)


        val stopButton = findViewById<Button>(R.id.stop_button)
        /*
        val cameraButton = findViewById<Button>(R.id.camera_button)
        var imageUri:Uri? = null

        cameraButton.setOnClickListener {
            val outputImage = File(externalCacheDir,"output_image.jpg")
            try {
                if (outputImage.exists()){
                    outputImage.delete()
                }
                outputImage.createNewFile()
            }catch (e:IOException){
                e.printStackTrace()
            }

           val itent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
            startActivityForResult(intent,SecondVisitActivity.TAKE_PHOTO)
        }*/
    }


}