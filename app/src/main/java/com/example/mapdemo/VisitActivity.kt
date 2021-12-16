package com.example.mapdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class VisitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.visit_first)
        val visitText = findViewById<TextView>(R.id.new_visit_text)
        val editView = findViewById<EditText>(R.id.title_text)
        val explainText = findViewById<TextView>(R.id.explain_text)
        val startButton = findViewById<Button>(R.id.start_button)
        val sdf = SimpleDateFormat("dd/M/yyyy/   hh:mm:ss")
        val currentDate: String = sdf.format(Date())

        explainText.text = currentDate
        startButton.setOnClickListener {
            val intent = Intent(this, SecondVisitActivity::class.java)
            intent.putExtra("title", visitText.text)
            startActivity(intent)
        }
    }



}