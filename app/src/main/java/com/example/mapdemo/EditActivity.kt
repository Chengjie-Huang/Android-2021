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

class EditActivity : AppCompatActivity() {

    lateinit var daoObj: ImageDataDao
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_photo)

        daoObj = (this@EditActivity.application as ImageApplication)
            .databaseObj.imageDataDao()
        val bundle: Bundle? = intent.extras
        var position = -1

        if (bundle != null) {
            // this is the image position in the itemList
            position = bundle.getInt("position")
            if (position != -1) {
                val imageView = findViewById<ImageView>(R.id.edit_image)
                val titleEditToolbar = findViewById<Toolbar>(R.id.editor_toolbar)
                val titleTextInput = findViewById<TextInputEditText>(R.id.edit_image_title)
                val descriptionTextInput =
                    findViewById<TextInputEditText>(R.id.edit_image_description)

                makeButtonListeners(position)

                MyAdapter.items[position].let {
                    imageView.setImageBitmap(it.thumbnail)

                    titleEditToolbar.title = it.imageTitle
                    titleTextInput.setText(it.imageTitle)
                    descriptionTextInput.setText(it.imageDescription ?: "N/A")
                }
            }
        }
    }

    private fun makeButtonListeners(position: Int) {
        var id = MyAdapter.items[position].id
        val cancelButton: Button = findViewById(R.id.cancel_button)
        cancelButton.setOnClickListener {
            this@EditActivity.finish()
        }

        // Delete button listener
        val deleteButton: Button = findViewById(R.id.delete_button)
        deleteButton.setOnClickListener {
            scope.launch(Dispatchers.IO) {
                async { daoObj.delete(MyAdapter.items[position]) }
                    .invokeOnCompletion {
                        MyAdapter.items.removeAt(position)
                        val intent = Intent()
                            .putExtra("position", position)
                            .putExtra("id", id)
                            .putExtra("deletion_flag", 1)
                        this@EditActivity.setResult(RESULT_OK, intent)
                        this@EditActivity.finish()
                    }
            }
        }

        // Save button listener
        val saveButton: Button = findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            val descriptionTextInput =
                findViewById<TextInputEditText>(R.id.edit_image_description)
            MyAdapter.items[position].imageDescription = descriptionTextInput.text.toString()
            val titleTextInput = findViewById<TextInputEditText>(R.id.edit_image_title)
            MyAdapter.items[position].imageTitle = titleTextInput.text.toString()

            scope.launch(Dispatchers.IO) {
                async { daoObj.update(MyAdapter.items[position]) }
                    .invokeOnCompletion {
                        val intent = Intent()
                            .putExtra("position", position)
                            .putExtra("id", id)
                            .putExtra("deletion_flag", 0)
                        this@EditActivity.setResult(RESULT_OK, intent)
                        this@EditActivity.finish()
                    }
            }
        }
    }
}