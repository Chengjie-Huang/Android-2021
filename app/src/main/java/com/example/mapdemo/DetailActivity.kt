package com.example.mapdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mapdemo.data.ImageDataDao
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class DetailActivity : AppCompatActivity() {
    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    lateinit var daoObj: ImageDataDao

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

        if (bundle != null) {
            // this is the image position in the itemList
            position = bundle.getInt("position")
            displayData(position)

    }
}

    private fun displayData(position: Int) {
        if (position != -1) {
            val imageView = findViewById<ImageView>(R.id.detail_image)
            val titleTextView = findViewById<TextView>(R.id.detail_text_title)
            val dateTextView = findViewById<TextView>(R.id.detail_text_date)
            val descriptionTextView = findViewById<TextView>(R.id.detail_text_description)

            imageView.setImageBitmap(PhotosAdapter.items[position].thumbnail!!)
            titleTextView.text = PhotosAdapter.items[position].imageTitle
            dateTextView.text = PhotosAdapter.items[position].imageDate
            descriptionTextView.text = PhotosAdapter.items[position].imageDescription

            val fabEdit: FloatingActionButton = findViewById(R.id.detail_change_button)
            fabEdit.setOnClickListener(View.OnClickListener {
                startForResult.launch(
                    Intent( this, DetailEditActivity::class.java).apply {
                        putExtra("position", position)
                    }
                )
            })
        }
    }
}
