package id.co.evolution.trackingdevice.presentation.state

import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.domain.model.BleDeviceHistoryEntity
import id.co.evolution.trackingdevice.domain.model.SignalCategory

data class BleUiState(
    val currentTab: Int = 0, // 0 = Home, 1 = Riwayat
    val liveDevices: List<BleDeviceEntity> = emptyList(), // In-memory live scanning, tidak disimpan ke lokal
    val historyDevices: List<BleDeviceHistoryEntity> = emptyList(), // Data tersimpan di Room DB
    val isScanning: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
) {
    // Properti bantuan untuk kompatibilitas jika ada screen lain yang memakai `devices`
    val devices: List<BleDeviceEntity>
        get() = liveDevices

    // Live devices yang sudah difilter berdasarkan query dan diurutkan sinyal terkuat
    val filteredLiveDevices: List<BleDeviceEntity>
        get() {
            val list = if (searchQuery.isBlank()) {
                liveDevices
            } else {
                liveDevices.filter {
                    (it.deviceName?.contains(searchQuery, ignoreCase = true) == true) ||
                            it.macAddress.contains(searchQuery, ignoreCase = true)
                }
            }
            return list.sortedByDescending { it.rawRssi }
        }
    val signalCategoryList:List<SignalCategory>
        get() {
            val list = if (searchQuery.isBlank()) {
                liveDevices
            } else {
                liveDevices.filter {
                    true
                }
            }
            return list.sortedByDescending { it.rawRssi }.map { it.signalCategory }.distinct()
        }
    // History devices yang sudah difilter berdasarkan query
    val filteredHistoryDevices: List<BleDeviceHistoryEntity>
        get() {
            val list = if (searchQuery.isBlank()) {
                historyDevices
            } else {
                historyDevices.filter {
                    (it.deviceName?.contains(searchQuery, ignoreCase = true) == true) ||
                            it.macAddress.contains(searchQuery, ignoreCase = true)
                }
            }
            return list.sortedByDescending { it.rawRssi }
        }
}
