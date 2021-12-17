/**
 * DetailEditActivity,
 * this interface realizes the function of editing photos,
 * and can modify the Title, Date, and Description information of the photos and store them in the database.
 */
package com.example.mapdemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.mapdemo.data.ImageData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class DetailEditActivity :AppCompatActivity() {
    private var mViewModel: ImageDataViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_layout_main)
        this.mViewModel = ViewModelProvider(this)[ImageDataViewModel::class.java]

        val editHomeButton = findViewById<FloatingActionButton>(R.id.edit_home_button)
        val bundle: Bundle? = intent.extras
        var position = -1
        var from: Int

        editHomeButton.setOnClickListener(View.OnClickListener{
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        })

        if (bundle != null) {
            // this is the image position in the itemList
            from = bundle.getInt("from")
            position = bundle.getInt("position")
            val imageView = findViewById<ImageView>(R.id.edit_image)
            val titleInput = findViewById<TextInputEditText>(R.id.edit_title)
            val dateInput = findViewById<TextInputEditText>(R.id.edit_date)
            val descriptionInput = findViewById<TextInputEditText>(R.id.edit_description)

            if (from == 0) {
                val image = bundle.getString("imgUri")
                val title = bundle.getString("imgTitle")
                val description = bundle.getString("imgDescription")
                val date = bundle.getString("imgDate")

                imageView.setImageURI(Uri.parse(image))
                titleInput.setText(title)
                dateInput.setText(date)
                descriptionInput.setText(description ?: "N/A")

                makeButtonListeners(from, position)
            } else if (from == 1) {
                makeButtonListeners(from, position)

                PhotosAdapter.items[position].let {
                    imageView.setImageBitmap(it.thumbnail)

                    titleInput.setText(it.imageTitle)
                    dateInput.setText(it.imageDate)
                    descriptionInput.setText(it.imageDescription ?: "N/A")
                }
            }
        }
    }

    private fun makeButtonListeners(from: Int, position: Int) {
        val imageData: ImageData
        if (from == 0) {
            imageData = MapsActivity.myDataset[position]
        } else {
            imageData = PhotosAdapter.items[position]
        }

        val id = imageData.id
        val cancelButton: Button = findViewById(R.id.edit_cancel_button)
        cancelButton.setOnClickListener {
            this@DetailEditActivity.finish()
        }

        // Delete button listener
        val deleteButton: Button = findViewById(R.id.edit_delete_button)
        deleteButton.setOnClickListener {
            this.mViewModel!!.deleteImageData(imageData)
            val intent = Intent()
                .putExtra("position", position)
                .putExtra("id", id)
                .putExtra("deletion_flag", 1)
            this@DetailEditActivity.setResult(Activity.RESULT_OK, intent)
            this@DetailEditActivity.finish()
        }

        // Save button listener
        val saveButton: Button = findViewById(R.id.edit_save_button)
        saveButton.setOnClickListener {

            val titleInput = findViewById<TextInputEditText>(R.id.edit_title)
            imageData.imageTitle = titleInput.text.toString()
            val dateInput = findViewById<TextInputEditText>(R.id.edit_date)
            imageData.imageDate = dateInput.text.toString()
            val descriptionInput = findViewById<TextInputEditText>(R.id.edit_description)
            imageData.imageDescription = descriptionInput.text.toString()

            this.mViewModel!!.updateImageData(imageData)
            val intent = Intent()
                .putExtra("position", position)
                .putExtra("id", id)
                .putExtra("deletion_flag", 1)
            this@DetailEditActivity.setResult(Activity.RESULT_OK, intent)
            this@DetailEditActivity.finish()
        }
    }
}