package id.co.evolution.trackingdevice.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.co.evolution.trackingdevice.data.local.DatabaseBle
import id.co.evolution.trackingdevice.data.local.dao.BleDeviceDao
import id.co.evolution.trackingdevice.data.local.dao.BleDeviceHistoryDao
import id.co.evolution.trackingdevice.domain.repository.BleDeviceRepository
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): DatabaseBle {
        return Room.databaseBuilder(
            context,
            DatabaseBle::class.java,
            "ble_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideBleDeviceDao(database: DatabaseBle): BleDeviceDao {
        return database.bleDeviceDao()
    }

    @Provides
    fun provideBleDeviceHistoryDao(database: DatabaseBle): BleDeviceHistoryDao {
        return database.bleDeviceHistoryDao()
    }

    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter? {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

    @Provides
    @Singleton
    fun provideBleRepository(
        dao: BleDeviceDao,
        historyDao: BleDeviceHistoryDao
    ): BleDeviceRepository {
        return BleDeviceRepository(dao, historyDao)
    }
}