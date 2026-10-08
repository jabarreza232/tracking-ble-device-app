package id.co.evolution.trackingdevice.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import id.co.evolution.trackingdevice.data.local.dao.BleDeviceDao
import id.co.evolution.trackingdevice.data.local.dao.BleDeviceHistoryDao
import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.domain.model.BleDeviceHistoryEntity

@Database(
    entities = [BleDeviceEntity::class, BleDeviceHistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class DatabaseBle : RoomDatabase() {
    abstract fun bleDeviceDao(): BleDeviceDao
    abstract fun bleDeviceHistoryDao(): BleDeviceHistoryDao
}
