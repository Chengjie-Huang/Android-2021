package com.example.mapdemo

import android.os.Bundle
import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.view.View
import android.widget.*

import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback

import com.example.mapdemo.databinding.ActivityMapsBinding
import com.example.mapdemo.PermissionUtils.PermissionDeniedDialog.Companion.newInstance
import com.example.mapdemo.PermissionUtils.RationaleDialog.Companion.systemPermissionCode
import com.example.mapdemo.PermissionUtils.isPermissionGranted
import com.example.mapdemo.PermissionUtils.requestPermission
import com.example.mapdemo.data.ImageData
import kotlinx.coroutines.*
import pl.aprilapps.easyphotopicker.MediaFile

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    OnMyLocationClickListener, OnRequestPermissionsResultCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var locatePermissionDenied = false
    private var readPermissionDenied = false
    private var writePermissionDenied = false
    private var cameraPermissionDenied = false

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        changeLocationButtonPosition(mapFragment)

        // Operation when clicking on the main list button
        val mainButton = findViewById<Button>(R.id.main_list_button)
        val searchButton = findViewById<Button>(R.id.search_button)
        val visitButton = findViewById<Button>(R.id.visit_button)
        val buttonList: Array<Button> = arrayOf<Button>(searchButton, visitButton)
        mainButton.setOnClickListener(View.OnClickListener {
            showButtonList(buttonList, mainButton)
            mainButton.text = if (mainButtonClicks % 2 == 0) "-" else "+"
            mainButtonClicks += 1
        })
        visitButton.setOnClickListener {
            val intent = Intent(this, VisitActivity::class.java)
            startActivity(intent)
        }
        searchButton.setOnClickListener {
            startActivity( Intent(this, SearchActivity::class.java))
            //Jump to the search page
        }
        initMapsView()
        initMapsData()
    }

    /**
     * Show the main button list by animation.
     */
    @SuppressLint("Recycle")
    private fun showButtonList(buttonList: Array<Button>, mainButton: Button) {
        var i = 1
        val mainButtonY = mainButton.y
        for (button in buttonList) {
            val objAnimatorY: ObjectAnimator
            if (mainButtonClicks % 2 == 0) {
                button.visibility = View.VISIBLE
                objAnimatorY = ObjectAnimator.ofFloat(button, "y", button.y,
                    button.y - i * BUTTON_Y_OFF_AXIS)
            } else {
                objAnimatorY = ObjectAnimator.ofFloat(button, "y", button.y,
                    button.y + (mainButtonY - button.y))
                objAnimatorY.addListener(object: AnimatorListenerAdapter() {
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

    /**
     * Change the default position of "my location button" to bottom left.
     */
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
     * Enable four permissions:
     * 1. Self Location
     * 2. Reading external storage
     * 3. Writing external storage
     * 4. Camera
     */
    private fun enableAllPermission() {
        if (!::map.isInitialized) return
        for ((i, code) in PermissionCode.values().withIndex()) {
            if (ContextCompat.checkSelfPermission(this, systemPermissionCode[i]) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission(this, code.ordinal, systemPermissionCode[i], true)
            } else if (code == PermissionCode.LOCATION) {
                map.isMyLocationEnabled = true
            }
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

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != PermissionCode.LOCATION.ordinal) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            map.isMyLocationEnabled = true
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            locatePermissionDenied = true
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (locatePermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            locatePermissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
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
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap ?: return
        enableAllPermission()
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        enum class PermissionCode {
            LOCATION, READ, WRITE, CAMERA
        }
        private var mainButtonClicks = 0
        private const val BUTTON_Y_OFF_AXIS = 120
    }
    //增加预览栏
    private var horizon_view: HorizontalScrollView? = null
    private var linear_view: LinearLayout? = null
    private var grid_view: GridLayout? = null

    //直接使用linerlayout

    private fun initMapsView(){
        horizon_view = findViewById(R.id.maps_horizon)
        linear_view = findViewById(R.id.maps_linear)
    }

    //图片测试数组之后删除！！！之后改为数据库中的图片集
    var ImageView = intArrayOf(
        R.drawable.walk,
        R.drawable.joe1,
        R.drawable.joe1,
        R.drawable.joe1,
        R.drawable.joe1,
        R.drawable.joe1,
        R.drawable.joe1,
        R.drawable.joe1,
        R.drawable.joe1
    )

    private fun initMapsData(){
        for (i in ImageView.indices) {
            val img = ImageView(this@MapsActivity)
            val params = LinearLayout.LayoutParams(500, LinearLayout.LayoutParams.MATCH_PARENT)
            params.setMargins(5, 0, 5, 0)
            img.scaleType = android.widget.ImageView.ScaleType.FIT_XY
            img.layoutParams = params
            //添加点击事件(跳转界面)
            img.setOnClickListener {
                startActivity( Intent(this, DetailActivity().javaClass))
            }
            img.setImageResource(ImageView[i])
            linear_view!!.setBackgroundColor(Color.argb(255,113,191,234))
            linear_view!!.addView(img)
        }
    }

}


