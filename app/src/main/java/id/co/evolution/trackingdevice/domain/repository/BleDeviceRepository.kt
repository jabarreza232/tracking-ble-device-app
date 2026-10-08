package id.co.evolution.trackingdevice.domain.repository

import id.co.evolution.trackingdevice.data.local.dao.BleDeviceDao
import id.co.evolution.trackingdevice.data.local.dao.BleDeviceHistoryDao
import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.domain.model.BleDeviceHistoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BleDeviceRepository @Inject constructor(
    private val dao: BleDeviceDao,
    private val historyDao: BleDeviceHistoryDao
) {
    val allDevicesFlow: Flow<List<BleDeviceEntity>> = dao.getAllDevices()
    val allHistoryFlow: Flow<List<BleDeviceHistoryEntity>> = historyDao.getAllHistory()

    suspend fun saveDevice(device: BleDeviceEntity) {
        dao.insertDevice(device)
    }

    suspend fun saveHistory(history: BleDeviceHistoryEntity) {
        historyDao.insertHistory(history)
    }

    suspend fun saveAllHistory(historyList: List<BleDeviceHistoryEntity>) {
        historyDao.insertAllHistory(historyList)
    }

    fun searchHistory(query: String): Flow<List<BleDeviceHistoryEntity>> {
        return historyDao.searchHistory(query)
    }

    suspend fun clearHistory() {
        historyDao.clearHistory()
    }

    suspend fun deleteHistoryById(id: Long) {
        historyDao.deleteHistoryById(id)
    }
}
