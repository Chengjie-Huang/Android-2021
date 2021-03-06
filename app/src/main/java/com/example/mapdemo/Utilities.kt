/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield.
 * All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com.example.mapdemo

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Utilities {
    /**
     * it converts a number of mseconds since 1.1.1970 (epoch) to a current string date
     * @param actualTimeInMseconds a time in msecs for the UTC time zone
     * @return a time string of type HH:mm:ss such as 23:12:54.
     */
    fun mSecsToString(actualTimeInMseconds: Long): String {
        val date = Date(actualTimeInMseconds)
        return with(SimpleDateFormat("HH:mm:ss") as DateFormat) {
            this.timeZone = TimeZone.getTimeZone("UTC")
            this.format(date)
        }
        // val formatter: DateFormat = SimpleDateFormat("HH:mm:ss")
        // formatter.timeZone = TimeZone.getTimeZone("UTC")
        // return formatter.format(date)
    }
}