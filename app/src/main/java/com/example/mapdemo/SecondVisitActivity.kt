package com.example.mapdemo

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.mapdemo.databinding.ActivityMapsBinding
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.DateFormat
import java.util.*
import com.google.android.gms.location.LocationRequest
import pl.aprilapps.easyphotopicker.*

import com.example.mapdemo.data.ImageData
import com.google.android.gms.common.api.ApiException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat

class SecondVisitActivity : AppCompatActivity(), GoogleMap.OnMyLocationClickListener,
    GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {
    private lateinit var ctx: Context
    private lateinit var easyImage: EasyImage
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val mapView: MapView? = null
    private var mViewModel: ImageDataViewModel? = null
    private var allowUpdateLocate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.visit_second)
        setActivity(this)
        setContext(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        changeLocationButtonPosition(mapFragment)

        initEasyImage()
        this.mViewModel = ViewModelProvider(this)[ImageDataViewModel::class.java]

        // Operation when clicking on the main list button
        val mainButton = findViewById<FloatingActionButton>(R.id.main_list_second_button)
        val startButton = findViewById<FloatingActionButton>(R.id.start_button)
        val stopButton = findViewById<FloatingActionButton>(R.id.stop_button)
        val photoButton = findViewById<FloatingActionButton>(R.id.photo_button)
        val buttonList: Array<FloatingActionButton> = arrayOf<FloatingActionButton>(stopButton, startButton,photoButton)
        mainButton.setOnClickListener(View.OnClickListener {
            showButtonList(buttonList, mainButton)
            mainButtonClicks += 1
        })
        photoButton.setOnClickListener(View.OnClickListener {
            easyImage.openChooser(this@SecondVisitActivity)
        })
        startButton.setOnClickListener(View.OnClickListener {
            startLocationUpdates()
            allowUpdateLocate = true
        })
        stopButton.setOnClickListener(View.OnClickListener {
            stopLocationUpdates()
            allowUpdateLocate = false
        })
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        Log.e("Location update", "Starting...")
        val intent = Intent(ctx, LocationService::class.java)
        mLocationPendingIntent =
            PendingIntent.getService(ctx,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        Log.e("IntentService", "Getting...")

        val locationTask = mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationPendingIntent!!
        )
        locationTask.addOnFailureListener { e ->
            if (e is ApiException) {
                e.message?.let { Log.w("MapsActivity", it) }
            } else {
                Log.w("MapsActivity", e.message!!)
            }
        }
        locationTask.addOnCompleteListener {
            Log.d(
                "MapsActivity",
                "starting gps successful!"
            )
        }
    }

    /**
     * it stops the location updates
     */
    private fun stopLocationUpdates() {
        Log.e("Location", "update stop")
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (allowUpdateLocate) {
            mLocationRequest = LocationRequest.create().apply {
                interval = 1000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            startLocationUpdates()
        }
    }

    private var mCurrentLocation: Location? = null
    private var mLastUpdateTime: String? = null
    private var mLocationPendingIntent: PendingIntent? = null
    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            mCurrentLocation = locationResult.getLastLocation()
            mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
            Log.d("MAP", "new location " + mCurrentLocation.toString())
            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        mCurrentLocation!!.latitude,
                        mCurrentLocation!!.longitude
                    )
                ).title(mLastUpdateTime)
            )
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        mCurrentLocation!!.latitude,
                        mCurrentLocation!!.longitude
                    ), 14.0f
                )
            )
        }
    }

    private fun initEasyImage() {
        easyImage = EasyImage.Builder(this)
            .setChooserTitle("Pick media")
            .setFolderName("EasyImage sample")
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(true)
            .setCopyImagesToPublicGalleryFolder(true)
            .build()
    }

    private fun changeLocationButtonPosition(mapFragment: SupportMapFragment) {
        val mapView = mapFragment.view as View
        val locationButton = (mapView.findViewById<View>(Integer.parseInt("1")).parent
                as View).findViewById<View>(Integer.parseInt("2"))
        val locationButtonLayout = locationButton.layoutParams as (RelativeLayout.LayoutParams)
        // Position on left bottom
        locationButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        locationButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        locationButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
        locationButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        locationButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_END, 0)
        locationButtonLayout.addRule(RelativeLayout.ALIGN_END, 0)
        locationButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        locationButtonLayout.setMargins(30, 0, 0, 80)
    }
    /**
     * Show the main button list by animation.
     */
    @SuppressLint("Recycle")
    private fun showButtonList(buttonList: Array<FloatingActionButton>, mainButton: FloatingActionButton) {
        var i = 1
        val mainButtonY = mainButton.y
        for (button in buttonList) {
            val objAnimatorY: ObjectAnimator
            if (SecondVisitActivity.mainButtonClicks % 2 == 0) {
                button.visibility = View.VISIBLE
                objAnimatorY = ObjectAnimator.ofFloat(
                    button, "y", button.y,
                    button.y - i * SecondVisitActivity.BUTTON_Y_OFF_AXIS
                )
            } else {
                objAnimatorY = ObjectAnimator.ofFloat(
                    button, "y", button.y,
                    button.y + (mainButtonY - button.y)
                )
                objAnimatorY.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        button.visibility = View.GONE
                    }
                })
            }
            objAnimatorY.startDelay = 10
            objAnimatorY.start()
            i += 1
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap ?: return
        mMap.isMyLocationEnabled = true
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        easyImage.handleActivityResult(requestCode, resultCode, data, this,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    onPhotosReturned(imageFiles)
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    super.onImagePickerError(error, source)
                }

                override fun onCanceled(source: MediaSource) {
                    super.onCanceled(source)
                }
            }
        )
    }

    /**
     * add the selected images to the database
     * @param returnedPhotos
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun onPhotosReturned(returnedPhotos: Array<MediaFile>) {
        getImageData(returnedPhotos)
    }

    /**
     * given a list of photos, it creates a list of ImageData objects
     * we do not know how many elements we will have
     * @param returnedPhotos
     * @return
     */
    private fun getImageData(returnedPhotos: Array<MediaFile>) {
        val bundle = this.intent.extras
        val title : String = bundle?.get("visit_title").toString()
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate : String = sdf.format(Date())
        val latitue: Double?
        val longitude: Double?
        if (mCurrentLocation != null) {
            latitue = mCurrentLocation!!.latitude
            longitude = mCurrentLocation!!.longitude
        } else {
            latitue = 0.0
            longitude = 0.0
        }
//        val latitue : Double = mCurrentLocation!!.latitude
//        val longitude : Double = mCurrentLocation!!.longitude
        Log.i("Second visit","date: " + currentDate + " latitude: " + latitue)
        for (mediaFile in returnedPhotos) {
            var imageData = ImageData(
                imageTitle = title,
                imageUri = mediaFile.file.absolutePath,
                imageDate = currentDate,
                imageLatitude = latitue,
                imageLongitude = longitude
            )
//            Log.i("Second visit: ", imageData.toString())
            // Update the database with the newly created object
//            var id = insertData(imageData)
            this.mViewModel!!.insertNewImageData(imageData)
        }
    }

    private fun setContext(context: Context) {
        ctx = context
    }

    companion object {
        private var mainButtonClicks = 0
        private const val BUTTON_Y_OFF_AXIS = 120

        private var activity: AppCompatActivity? = null
        private lateinit var mMap: GoogleMap
        //private const val ACCESS_FINE_LOCATION = 123

        fun getActivity(): AppCompatActivity? {
            return activity
        }

        fun setActivity(newActivity: AppCompatActivity) {
            activity = newActivity
        }

        fun getMap(): GoogleMap {
            return mMap
        }
    }
}