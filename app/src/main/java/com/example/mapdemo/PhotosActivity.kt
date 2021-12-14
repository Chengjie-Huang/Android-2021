package com.example.mapdemo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapdemo.data.ImageData
import com.example.mapdemo.data.ImageDataDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.ArrayList

private var myDataset: MutableList<ImageData> = ArrayList<ImageData>()
private lateinit var daoObj: ImageDataDao
private lateinit var mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
private lateinit var mRecyclerView: RecyclerView
val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
class PhotosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photos_layout_main)

        // Log.d("TAG", "message")
        mRecyclerView = findViewById(R.id.grid_recycler_view)
        // set up the RecyclerView
        val numberOfColumns = 4
        mRecyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        mAdapter = MyAdapter(myDataset) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        mRecyclerView.adapter = mAdapter
    }
}
