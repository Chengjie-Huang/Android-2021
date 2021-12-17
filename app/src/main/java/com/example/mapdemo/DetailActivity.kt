/**
 * DetailActivity,
 * this interface displays a large picture of the photo,
 * and can display detailed information such as Title, Date, Location, Description,
 * and provides an edit button for the user to enable the user to learn and edit the photo.
 */
package com.example.mapdemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View

import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailActivity : AppCompatActivity() {
    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val position = result.data?.getIntExtra("position", -1)
            val id = result.data?.getIntExtra("id", -1)
            val del_flag = result.data?.getIntExtra("deletion_flag", -1)
            var intent = Intent().putExtra("position", position)
                .putExtra("id", id).putExtra("deletion_flag", del_flag)
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    this.setResult(result.resultCode, intent)
                    this.finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_layout_main)
        val bundle: Bundle? = intent.extras
        var position = -1
        var from: Int

        if (bundle != null) {
            from  = bundle.getInt("from")
            position = bundle.getInt("position")
            val imageView = findViewById<ImageView>(R.id.detail_image)
            val titleTextView = findViewById<TextView>(R.id.detail_text_title)
            val dateTextView = findViewById<TextView>(R.id.detail_text_date)
            val locationTextView = findViewById<TextView>(R.id.detail_text_path)
            val descriptionTextView = findViewById<TextView>(R.id.detail_text_description)
            val fabEdit: FloatingActionButton = findViewById(R.id.detail_change_button)

            if (from == 0) {
                val image = bundle.getString("imgUri")
                val title = bundle.getString("imgTitle")
                val latitude = bundle.getDouble("imgLat")
                val longitude = bundle.getDouble("imgLong")
                val description = bundle.getString("imgDescription")
                val date = bundle.getString("imgDate")
                imageView.setImageURI(Uri.parse(image))
                titleTextView.text = title
                dateTextView.text = date
                locationTextView.text = latitude.toString() + ", " + longitude.toString()
                descriptionTextView.text = description

                fabEdit.setOnClickListener(View.OnClickListener {
                    startForResult.launch(
                        Intent( this, DetailEditActivity::class.java).apply {
                            putExtra("from", 0)
                            putExtra("imgUri", image)
                            putExtra("imgTitle", title)
                            putExtra("imgDescription", description)
                            putExtra("imgDate", date)
                        }
                    )
                })
            } else if (from == 1) {
                val latitude = PhotosAdapter.items[position].imageLatitude.toString()
                val longitude = PhotosAdapter.items[position].imageLongitude.toString()

                imageView.setImageBitmap(PhotosAdapter.items[position].thumbnail!!)
                titleTextView.text = PhotosAdapter.items[position].imageTitle
                dateTextView.text = PhotosAdapter.items[position].imageDate
                locationTextView.text = latitude + ", " + longitude
                descriptionTextView.text = PhotosAdapter.items[position].imageDescription

                fabEdit.setOnClickListener(View.OnClickListener {
                    startForResult.launch(
                        Intent( this, DetailEditActivity::class.java).apply {
                            putExtra("from", 1)
                            putExtra("position", position)
                        }
                    )
                })
            }

        }
    }
}
