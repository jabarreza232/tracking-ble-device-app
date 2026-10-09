package id.co.evolution.trackingdevice.presentation.state

import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.domain.model.SignalCategory

sealed class BleUiEvent {
    data object StartScan : BleUiEvent()
    data object StopScan : BleUiEvent()
    data class OnError(val message: String) : BleUiEvent()
    data class OnTabChanged(val tabIndex: Int) : BleUiEvent()
    data class OnSearchQueryChanged(val query: String) : BleUiEvent()
    data class SaveDeviceToHistory(val device: BleDeviceEntity) : BleUiEvent()
    data object SaveAllLiveDevicesToHistory : BleUiEvent()
    data object ClearHistory : BleUiEvent()
    data class DeleteHistoryItem(val id: Long) : BleUiEvent()
    data class OnCategoryFilterSelected(val category: SignalCategory?) : BleUiEvent()
    data class OnMinRssiFilterSelected(val minRssi: Int?) : BleUiEvent()
    data object ToggleDarkTheme : BleUiEvent()
    data object ResetFilters : BleUiEvent()
}
