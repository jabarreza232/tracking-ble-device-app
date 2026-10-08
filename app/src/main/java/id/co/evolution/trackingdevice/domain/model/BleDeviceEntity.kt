package id.co.evolution.trackingdevice.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "ble_devices")
data class BleDeviceEntity(
    @PrimaryKey val macAddress: String, // Pengenal Unik
    val deviceName: String?,
    val rawRssi: Int,
    val estimatedDistance: Double,
    val lastSeenTimestamp: Long = System.currentTimeMillis()
) : Parcelable {
    @delegate:Ignore
    val signalCategory: SignalCategory by lazy {
        SignalCategory.fromRssi(rawRssi)
    }
}
