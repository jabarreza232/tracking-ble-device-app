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
    val searchQuery: String = "",
    val selectedCategory: SignalCategory? = null,
    val minRssiFilter: Int? = null,
    val isDarkTheme: Boolean = false
) {
    // Properti bantuan untuk kompatibilitas jika ada screen lain yang memakai `devices`
    val devices: List<BleDeviceEntity>
        get() = liveDevices

    // Live devices yang sudah difilter berdasarkan query, kategori sinyal, rssi, dan diurutkan sinyal terkuat
    val filteredLiveDevices: List<BleDeviceEntity>
        get() {
            var list = if (searchQuery.isBlank()) {
                liveDevices
            } else {
                liveDevices.filter {
                    (it.deviceName?.contains(searchQuery, ignoreCase = true) == true) ||
                            it.macAddress.contains(searchQuery, ignoreCase = true)
                }
            }

            if (selectedCategory != null) {
                list = list.filter { it.signalCategory == selectedCategory }
            }

            if (minRssiFilter != null) {
                list = list.filter { it.rawRssi >= minRssiFilter }
            }

            return list.sortedByDescending { it.rawRssi }
        }

    val signalCategoryList: List<SignalCategory>
        get() {
            return liveDevices.map { it.signalCategory }.distinct().sortedBy { it.ordinal }
        }

    // History devices yang sudah difilter berdasarkan query, kategori sinyal, dan rssi
    val filteredHistoryDevices: List<BleDeviceHistoryEntity>
        get() {
            var list = if (searchQuery.isBlank()) {
                historyDevices
            } else {
                historyDevices.filter {
                    (it.deviceName?.contains(searchQuery, ignoreCase = true) == true) ||
                            it.macAddress.contains(searchQuery, ignoreCase = true)
                }
            }

            if (selectedCategory != null) {
                list = list.filter { SignalCategory.fromRssi(it.rawRssi) == selectedCategory }
            }

            if (minRssiFilter != null) {
                list = list.filter { it.rawRssi >= minRssiFilter }
            }

            return list.sortedByDescending { it.rawRssi }
        }
}
