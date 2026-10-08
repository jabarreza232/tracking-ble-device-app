package id.co.evolution.trackingdevice.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.co.evolution.trackingdevice.domain.model.BleDeviceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BleDeviceHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(device: BleDeviceHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHistory(devices: List<BleDeviceHistoryEntity>)

    @Query("SELECT * FROM ble_device_history ORDER BY rawRssi DESC, timestamp DESC")
    fun getAllHistory(): Flow<List<BleDeviceHistoryEntity>>

    @Query("SELECT * FROM ble_device_history WHERE deviceName LIKE '%' || :query || '%' OR macAddress LIKE '%' || :query || '%' ORDER BY rawRssi DESC")
    fun searchHistory(query: String): Flow<List<BleDeviceHistoryEntity>>

    @Query("DELETE FROM ble_device_history")
    suspend fun clearHistory()

    @Query("DELETE FROM ble_device_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)
}
