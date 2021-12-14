package com.example.mapdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.maps.SupportMapFragment

class VisitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.visit_first)
        val visitText = findViewById<TextView>(R.id.new_visit_text)
        val editView = findViewById<EditText>(R.id.title_text)
        val explainText = findViewById<TextView>(R.id.explain_text)
        val startButton = findViewById<Button>(R.id.start_button)
        val mNew: EditText = findViewById(R.id.title_text)

        startButton.setOnClickListener {
            val m_New_text = mNew.text
            val intent = Intent(this, SecondVisitActivity::class.java)
            startActivity(intent)
        }
    }
}