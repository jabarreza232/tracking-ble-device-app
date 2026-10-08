package id.co.evolution.trackingdevice.utils

import kotlin.math.pow

object Tools {
    fun calculateDistance(rssi: Int, txPower: Int = -59): Double {
        if (rssi == 0) return -1.0
        val ratio = rssi * 1.0 / txPower
        return if (ratio < 1.0) {
            ratio.pow(10.0)
        } else {
            val pathLossExponent = 2.0
            10.0.pow((txPower - rssi) / (10.0 * pathLossExponent))
        }
    }
}