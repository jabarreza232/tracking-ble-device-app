package id.co.evolution.trackingdevice.presentation.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.domain.model.BleDeviceHistoryEntity
import id.co.evolution.trackingdevice.domain.repository.BleDeviceRepository
import id.co.evolution.trackingdevice.presentation.state.BleUiEvent
import id.co.evolution.trackingdevice.presentation.state.BleUiState
import id.co.evolution.trackingdevice.utils.Tools.calculateDistance
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class BleViewModel @Inject constructor(
    private val repository: BleDeviceRepository,
    private val bluetoothAdapter: BluetoothAdapter?
) : ViewModel() {

    private val _uiState = MutableStateFlow(BleUiState())
    val uiState: StateFlow<BleUiState> = _uiState.asStateFlow()

    private val scanner = bluetoothAdapter?.bluetoothLeScanner

    private val liveDevicesMap = ConcurrentHashMap<String, BleDeviceEntity>()
    private val _liveDevices = MutableStateFlow<List<BleDeviceEntity>>(emptyList())
    private val updateChannel = Channel<Unit>(Channel.CONFLATED)
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission") override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val address = result.device.address
            val rssi = result.rssi
            val distance = calculateDistance(rssi)
            val rawBytesName = parseDeviceNameFromBytes(result.scanRecord?.bytes)
            val name = result.device.name
                ?: result.scanRecord?.deviceName
                ?: rawBytesName
                ?: "Unknown Device"

            val entity = BleDeviceEntity(
                macAddress = address,
                deviceName = name,
                rawRssi = rssi,
                estimatedDistance = distance,
                lastSeenTimestamp = System.currentTimeMillis()
            )
            val isNewDevice = !liveDevicesMap.containsKey(entity.macAddress)
            // Simpan ke in-memory map saja (Home tidak disimpan ke lokal)
            if (isNewDevice) {
                saveDeviceToHistory(entity)
            }
            liveDevicesMap[address] = entity
            _liveDevices.value = liveDevicesMap.values.toList()

            updateChannel.trySend(Unit)

        }


        override fun onScanFailed(errorCode: Int) {
            onEvent(BleUiEvent.OnError("Scan gagal dengan kode: $errorCode"))
        }
    }

    init {
        // Coroutine 1: Throttled update untuk live devices UI
        viewModelScope.launch {
            updateChannel.consumeAsFlow()
                .sample(400) // Update UI maksimal setiap 400ms
                .collect {
                    val updatedList = liveDevicesMap.values.toList()
                    _uiState.update { currentState ->
                        currentState.copy(liveDevices = updatedList)
                    }
                }
        }

        // Coroutine 2: Collect history devices dari Room database
        viewModelScope.launch {
            repository.allHistoryFlow.collect { historyList ->
                _uiState.update { currentState ->
                    currentState.copy(historyDevices = historyList)
                }
            }
        }
    }

    fun onEvent(event: BleUiEvent) {
        when (event) {
            is BleUiEvent.StartScan -> startBleScan()
            is BleUiEvent.StopScan -> stopBleScan()
            is BleUiEvent.OnError -> {
                _uiState.update { it.copy(errorMessage = event.message) }
            }
            is BleUiEvent.OnTabChanged -> {
                _uiState.update { it.copy(currentTab = event.tabIndex) }
            }
            is BleUiEvent.OnSearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }
            is BleUiEvent.SaveDeviceToHistory -> {
                saveDeviceToHistory(event.device)
            }
            is BleUiEvent.SaveAllLiveDevicesToHistory -> {
                saveAllCurrentDevicesToHistory()
            }
            is BleUiEvent.ClearHistory -> {
                viewModelScope.launch {
                    repository.clearHistory()
                }
            }
            is BleUiEvent.DeleteHistoryItem -> {
                viewModelScope.launch {
                    repository.deleteHistoryById(event.id)
                }
            }
            is BleUiEvent.OnCategoryFilterSelected -> {
                _uiState.update { it.copy(selectedCategory = event.category) }
            }
            is BleUiEvent.OnMinRssiFilterSelected -> {
                _uiState.update { it.copy(minRssiFilter = event.minRssi) }
            }
            is BleUiEvent.ToggleDarkTheme -> {
                _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
            }
            is BleUiEvent.ResetFilters -> {
                _uiState.update { it.copy(selectedCategory = null, minRssiFilter = null, searchQuery = "") }
            }
        }
    }

    private fun saveDeviceToHistory(device: BleDeviceEntity) {
        viewModelScope.launch {
            val history = BleDeviceHistoryEntity(
                macAddress = device.macAddress,
                deviceName = device.deviceName,
                rawRssi = device.rawRssi,
                estimatedDistance = device.estimatedDistance,
                signalCategory = device.signalCategory.label,
                timestamp = System.currentTimeMillis()
            )
            repository.saveHistory(history)
        }
    }

    private fun saveAllCurrentDevicesToHistory() {
        viewModelScope.launch {
            val historyItems = liveDevicesMap.values.map { device ->
                BleDeviceHistoryEntity(
                    macAddress = device.macAddress,
                    deviceName = device.deviceName,
                    rawRssi = device.rawRssi,
                    estimatedDistance = device.estimatedDistance,
                    signalCategory = device.signalCategory.label,
                    timestamp = System.currentTimeMillis()
                )
            }
            if (historyItems.isNotEmpty()) {
                repository.saveAllHistory(historyItems)
            }
        }
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    @SuppressLint("MissingPermission")
    private fun startBleScan() {
        if (scanner == null) {
            onEvent(BleUiEvent.OnError("Bluetooth tidak didukung atau belum aktif"))
            return
        }
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner.startScan(null, settings, scanCallback)
        _uiState.update { it.copy(isScanning = true, errorMessage = null) }
    }
    fun parseDeviceNameFromBytes(scanRecord: ByteArray?): String? {
        if (scanRecord == null) return null
        var index = 0
        while (index < scanRecord.size) {
            val length = scanRecord[index++].toInt() and 0xFF
            if (length == 0) break
            if (index + length > scanRecord.size) break

            val type = scanRecord[index].toInt() and 0xFF
            // 0x08 = Shortened Local Name, 0x09 = Complete Local Name
            if (type == 0x08 || type == 0x09) {
                val nameBytes = scanRecord.copyOfRange(index + 1, index + length)
                return String(nameBytes, Charsets.UTF_8).trim()
            }
            index += length
        }
        return null
    }
    @SuppressLint("MissingPermission")
    private fun stopBleScan() {
        scanner?.stopScan(scanCallback)
        _uiState.update { it.copy(isScanning = false) }
    }

    override fun onCleared() {
        super.onCleared()
        stopBleScan()
    }
}
