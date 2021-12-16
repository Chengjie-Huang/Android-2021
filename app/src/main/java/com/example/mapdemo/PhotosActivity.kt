package com.example.mapdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import com.example.mapdemo.data.ImageData
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

        val homeButton = findViewById<FloatingActionButton>(R.id.home_button)

        this.mViewModel = ViewModelProvider(this)[ImageDataViewModel::class.java]
        mRecyclerView = findViewById(R.id.grid_recycler_view)

        val numberOfColumns = 4
        mRecyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        mAdapter = PhotosAdapter(myDataset) as RecyclerView.Adapter<RecyclerView.ViewHolder>
        mRecyclerView.adapter = mAdapter

        displayImage()
        homeButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        })

    }

    override fun onResume() {
        super.onResume()
        displayImage()
    }

    private fun displayImage() {
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
    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val pos = result.data?.getIntExtra("position", -1)!!
                val id = result.data?.getIntExtra("id", -1)!!
                val del_flag = result.data?.getIntExtra("deletion_flag", -1)!!
                if (pos != -1 && id != -1) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        when(del_flag){
                            -1, 0 -> mAdapter.notifyDataSetChanged()
                            else -> mAdapter.notifyItemRemoved(pos)
                        }
                    }
                }
            }
        }
}
