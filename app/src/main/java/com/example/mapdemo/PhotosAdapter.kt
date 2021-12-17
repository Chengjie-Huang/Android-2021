package com.example.mapdemo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.mapdemo.data.ImageData
import kotlinx.coroutines.*

class PhotosAdapter : RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    private lateinit var context: Context

    constructor(items: List<ImageData>) : super() {
        PhotosAdapter.items = items as MutableList<ImageData>
    }

    constructor(cont: Context, items: List<ImageData>) : super() {
        PhotosAdapter.items = items as MutableList<ImageData>
        context = cont
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflate the layout, initialize the View Holder
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_image,
            parent, false
        )

        val holder: PhotosAdapter.ViewHolder = ViewHolder(v)
        context = parent.context
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the
        // current row on the RecyclerVie
        if (items[position].thumbnail == null) {
            items[position].let {
                CoroutineScope(Dispatchers.Main).launch {
                    val bitmap =
                        decodeSampledBitmapFromResource(it.imageUri, 150, 150)
                    bitmap?.let {
                        items[position].thumbnail = it
                        holder.imageView.setImageBitmap(items[position].thumbnail)
                    }
                }
            }
        }
        holder.itemView.setOnClickListener(View.OnClickListener {
           val photosActivityContext = context as PhotosActivity
            photosActivityContext.startForResult.launch(
               Intent(context, DetailActivity::class.java).apply {
                   putExtra("position", position)
                   putExtra("from", 1)
                }
           )
        })
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById<View>(R.id.image_item) as ImageView
    }

    companion object {
        lateinit var items: MutableList<ImageData>
        /**
         * helper function to generate a bitmap object of a given size from an image's file path.
         */
        suspend fun decodeSampledBitmapFromResource(
            filePath: String,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap {
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()

            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(filePath, options);
        }

        /**
         * helper function to calculate an inSampleSize for resampling to achieve the right picture
         * density for a smaller size file.
         * See inSampleFile: https://developer.android.com/reference/android/graphics/BitmapFactory.Options#inSampleSize
         */
        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            // Raw height and width of image
            val height = options.outHeight;
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val halfHeight = (height / 2).toInt()
                val halfWidth = (width / 2).toInt()

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth
                ) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize.toInt();
        }
    }
}