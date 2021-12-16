package com.example.mapdemo

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.example.mapdemo.data.ImageDataDao
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class DetailEditActivity :AppCompatActivity() {
    lateinit var daoObj: ImageDataDao
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_layout_main)

        //加dao

        val bundle: Bundle? = intent.extras
        var position = -1

        if (bundle != null) {
            // this is the image position in the itemList
            position = bundle.getInt("position")
            if (position != -1) {
                val imageView = findViewById<ImageView>(R.id.edit_image)
                val titleInput = findViewById<TextInputEditText>(R.id.edit_title)
                val dateInput = findViewById<TextInputEditText>(R.id.edit_date)
                val descriptionInput = findViewById<TextInputEditText>(R.id.edit_description)

                makeButtonListeners(position)

                PhotosAdapter.items[position].let {
                    imageView.setImageBitmap(it.thumbnail)

                    titleInput.setText(it.imageTitle)
                    dateInput.setText(it.imageDate)
                    descriptionInput.setText(it.imageDescription ?: "N/A")
                }
            }
        }
    }
    fun makeButtonListeners(position: Int) {
        var id = PhotosAdapter.items[position].id
        val cancelButton: Button = findViewById(R.id.edit_cancel_button)
        cancelButton.setOnClickListener {
            this@DetailEditActivity.finish()
        }

        // Delete button listener
        val deleteButton: Button = findViewById(R.id.edit_delete_button)
        deleteButton.setOnClickListener {
            scope.launch(Dispatchers.IO) {
                async { daoObj.delete(PhotosAdapter.items[position]) }
                    .invokeOnCompletion {
                        PhotosAdapter.items.removeAt(position)
                        val intent = Intent()
                            .putExtra("position", position)
                            .putExtra("id", id)
                            .putExtra("deletion_flag", 1)
                        this@DetailEditActivity.setResult(Activity.RESULT_OK, intent)
                        this@DetailEditActivity.finish()
                    }
            }
        }
        // Save button listener
        val saveButton: Button = findViewById(R.id.edit_save_button)
        saveButton.setOnClickListener {

            val titleInput = findViewById<TextInputEditText>(R.id.edit_title)
            PhotosAdapter.items[position].imageTitle = titleInput.text.toString()
            val dateInput = findViewById<TextInputEditText>(R.id.edit_date)
            PhotosAdapter.items[position].imageDate = dateInput.text.toString()
            val descriptionInput = findViewById<TextInputEditText>(R.id.edit_description)
            PhotosAdapter.items[position].imageDescription = descriptionInput.text.toString()
            scope.launch(Dispatchers.IO) {
                async { daoObj.update(PhotosAdapter.items[position]) }
                    .invokeOnCompletion {
                        val intent = Intent()
                            .putExtra("position", position)
                            .putExtra("id", id)
                            .putExtra("deletion_flag", 0)
                        this@DetailEditActivity.setResult(Activity.RESULT_OK, intent)
                        this@DetailEditActivity.finish()
                    }
            }
        }
    }
}