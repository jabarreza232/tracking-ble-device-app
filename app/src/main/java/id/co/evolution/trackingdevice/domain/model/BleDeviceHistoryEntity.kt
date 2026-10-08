package id.co.evolution.trackingdevice.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ble_device_history")
data class BleDeviceHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val macAddress: String,
    val deviceName: String?,
    val rawRssi: Int,
    val estimatedDistance: Double,
    val signalCategory: String,
    val timestamp: Long = System.currentTimeMillis()
)
