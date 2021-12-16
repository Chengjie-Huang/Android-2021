package com.example.mapdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import com.example.mapdemo.data.ImageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotosActivity : AppCompatActivity() {
    private var mViewModel: ImageDataViewModel? = null
    private lateinit var mAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var mRecyclerView: RecyclerView
    private var myDataset: MutableList<ImageData> = ArrayList<ImageData>()
    private lateinit var title: String
    private lateinit var date: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photos_layout_main)

        this.mViewModel = ViewModelProvider(this)[ImageDataViewModel::class.java]
        mRecyclerView = findViewById(R.id.grid_recycler_view)

        val numberOfColumns = 4
        mRecyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        mAdapter = PhotosAdapter(myDataset) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        mRecyclerView.adapter = mAdapter

        displayImage()
    }

    override fun onResume() {
        super.onResume()
        displayImage()
    }

    fun displayImage() {
        var bundle = this.intent.extras
        title = bundle?.getString("search_title", "").toString()
        date = bundle?.getString("search_date", "").toString()
        if (title != "" && date != "") {
            this.mViewModel!!.getImageDataByTitleAndDate(title, date)!!.observe(this) { images ->
                images.let {
                    myDataset.clear();
                    myDataset.addAll(images!!)
                    mAdapter.notifyDataSetChanged()
                }
            }
        } else if (title != "") {
            this.mViewModel!!.getImageDataByTitle(title)!!.observe(this) { images ->
                images.let {
                    myDataset.clear();
                    myDataset.addAll(images!!)
                    mAdapter.notifyDataSetChanged()
                }
            }
        } else if (date != "") {
            this.mViewModel!!.getImageDataByDate(date)!!.observe(this) { images ->
                images.let {
                    myDataset.clear();
                    myDataset.addAll(images!!)
                    mAdapter.notifyDataSetChanged()
                }
            }
        } else {
            this.mViewModel!!.getImageDataToDisplay()!!.observe(this) { images ->
                images.let {
                    myDataset.clear();
                    myDataset.addAll(images!!)
                    mAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
