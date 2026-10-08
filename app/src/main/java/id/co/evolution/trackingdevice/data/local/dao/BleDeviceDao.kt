package id.co.evolution.trackingdevice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BleDeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: BleDeviceEntity)

    @Query("SELECT * FROM ble_devices ORDER BY estimatedDistance ASC")
    fun getAllDevices(): Flow<List<BleDeviceEntity>>
}