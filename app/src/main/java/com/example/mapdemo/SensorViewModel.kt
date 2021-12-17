/*
 * Copyright (c) 2021. This code has been developed by Temitope Adeosun, The University of Sheffield.
 * All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.sheffield.dcs.com31007_4510_6510.lab7

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SensorViewModel(application: Application): AndroidViewModel(application) {

    private var barometer: Barometer = Barometer(application)
    private var accelerometer: Accelerometer = Accelerometer(application, barometer)

    /**
     * Calls the needed sensor class to start monitoring the sensor data
     */
    fun startSensing() {
        accelerometer.startAccelerometerSensing()
    }


    /**
     * Calls the needed sensor class to stop monitoring the sensor data
     */
    fun stopSensing() {
        accelerometer.stopAccelerometerSensing()
    }

    /**
     * Func that exposes the pressure as LiveData to the View object
     * @return
     */
    fun retrievePressureData(): LiveData<Float>{
        return barometer.pressureReading
    }

    /**
     * Func that exposes the Accelerometer data as LiveData to the View object
     * @return
     */
    fun retrieveAccelerometerData(): LiveData<Pair<String,Map<String, Float>>>{
        return accelerometer.accelerometerReading
    }

    /**
     * Func that exposes the status change of the sensor monitoring
     */
    fun isStarted(): LiveData<Boolean>{
        return accelerometer.isStarted
    }
}