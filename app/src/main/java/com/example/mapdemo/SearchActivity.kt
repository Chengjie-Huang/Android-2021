package com.example.mapdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_layout_main)

        val title = findViewById<EditText>(R.id.search_text_title)
        val date = findViewById<EditText>(R.id.search_text_date)
        val searchButton = findViewById<Button>(R.id.searchlayout_button)
        searchButton.setOnClickListener {
            val intent = Intent(this, PhotosActivity::class.java)
            intent.putExtra("title", title.text)
            intent.putExtra("date", date.text)
            startActivity(intent)
        }
    }
}
