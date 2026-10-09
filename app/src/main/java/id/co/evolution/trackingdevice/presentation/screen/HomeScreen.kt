package id.co.evolution.trackingdevice.presentation.screen

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.co.evolution.trackingdevice.R
import id.co.evolution.trackingdevice.component.EnableBluetoothBottomSheet
import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.presentation.component.FilterSection
import id.co.evolution.trackingdevice.presentation.component.LiveDeviceCard
import id.co.evolution.trackingdevice.presentation.component.RadarAnimationView
import id.co.evolution.trackingdevice.presentation.component.SearchBar
import id.co.evolution.trackingdevice.presentation.state.BleUiEvent
import id.co.evolution.trackingdevice.presentation.state.BleUiState
import id.co.evolution.trackingdevice.presentation.viewmodel.BleViewModel
import id.co.evolution.trackingdevice.ui.theme.TrackingDeviceTheme

/**
 * Type-Safe representation for Home screen tabs.
 */
enum class HomeTab(
    val index: Int,
    @get:StringRes val titleResId: Int,
    val icon: ImageVector
) {
    HOME(0, R.string.home, Icons.Default.Radar),
    HISTORY(1, R.string.history, Icons.Default.History);

    companion object {
        fun fromIndex(index: Int): HomeTab = entries.find { it.index == index } ?: HOME
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BleViewModel = run {
        val activity = LocalContext.current as? ComponentActivity
        if (activity != null) hiltViewModel(activity) else hiltViewModel()
    },
    onNavigateDetailDevice: (device: BleDeviceEntity) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    var showBluetoothSheet by rememberSaveable { mutableStateOf(false) }
    var hasRequiredPermissions by rememberSaveable { mutableStateOf(false) }

    val permissionsToRequest = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val permissionErrorMessage = stringResource(R.string.permission_required)

    // Launcher untuk Izin Bluetooth
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        hasRequiredPermissions = permissionsMap.values.all { it }
        if (hasRequiredPermissions) {
            if (!viewModel.isBluetoothEnabled()) {
                showBluetoothSheet = true
            } else {
                viewModel.onEvent(BleUiEvent.StartScan)
            }
        } else {
            viewModel.onEvent(BleUiEvent.OnError(permissionErrorMessage))
        }
    }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onEvent(BleUiEvent.StartScan)
        }
    }

    HomeContent(
        state = state,
        onEvent = viewModel::onEvent,
        showBluetoothSheet = showBluetoothSheet,
        onDismissSheet = { showBluetoothSheet = false },
        onStartScanClick = {
            if (!hasRequiredPermissions) {
                permissionLauncher.launch(permissionsToRequest)
            } else if (!viewModel.isBluetoothEnabled()) {
                showBluetoothSheet = true
            } else {
                viewModel.onEvent(BleUiEvent.StartScan)
            }
        },
        onStopScanClick = {
            viewModel.onEvent(BleUiEvent.StopScan)
        },
        onEnableBluetoothClick = {
            showBluetoothSheet = false
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBluetoothLauncher.launch(intent)
        },
        onNavigateDetailDevice = onNavigateDetailDevice
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: BleUiState,
    onEvent: (BleUiEvent) -> Unit,
    onStartScanClick: () -> Unit,
    onStopScanClick: () -> Unit,
    showBluetoothSheet: Boolean,
    onDismissSheet: () -> Unit,
    onEnableBluetoothClick: () -> Unit,
    onNavigateDetailDevice: (device: BleDeviceEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val currentTab = HomeTab.fromIndex(state.currentTab)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { onEvent(BleUiEvent.ToggleDarkTheme) }) {
                        Icon(
                            imageVector = if (state.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = stringResource(R.string.switch_theme)
                        )
                    }
                    if (currentTab == HomeTab.HISTORY && state.historyDevices.isNotEmpty()) {
                        IconButton(onClick = { showClearConfirmDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = stringResource(R.string.delete_history_all),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // FAB hanya ditampilkan pada Tab Home
            if (currentTab == HomeTab.HOME) {
                if (state.isScanning) {
                    ExtendedFloatingActionButton(
                        onClick = onStopScanClick,
                        icon = { Icon(Icons.Default.Stop, contentDescription = stringResource(R.string.stop_scan)) },
                        text = { Text(stringResource(R.string.stop_scan)) },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    ExtendedFloatingActionButton(
                        onClick = onStartScanClick,
                        icon = { Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.start_scan)) },
                        text = { Text(stringResource(R.string.start_scan)) },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Menu: Type-Safe Tab Enumeration
            PrimaryTabRow(
                selectedTabIndex = currentTab.index,
                modifier = Modifier.fillMaxWidth()
            ) {
                HomeTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    val count = if (tab == HomeTab.HOME) state.liveDevices.size else state.historyDevices.size
                    val title = stringResource(tab.titleResId)

                    Tab(
                        selected = isSelected,
                        onClick = { onEvent(BleUiEvent.OnTabChanged(tab.index)) },
                        text = {
                            Text(
                                text = stringResource(R.string.tab_title_with_count, title, count),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = title
                            )
                        }
                    )
                }
            }

            // Pesan Error (jika ada)
            state.errorMessage?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Konten Berdasarkan Tab
            when (currentTab) {
                HomeTab.HOME -> HomeContent(
                    state = state,
                    onEvent = onEvent,
                    onNavigateDetailDevice = onNavigateDetailDevice
                )
                HomeTab.HISTORY -> HistoryDeviceScreen(
                    state = state,
                    onEvent = onEvent,
                    onNavigateDetailDevice = onNavigateDetailDevice
                )
            }
        }
    }

    // Dialog Konfirmasi Hapus Riwayat
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text(stringResource(R.string.delete_history_all)) },
            text = { Text(stringResource(R.string.message_history)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(BleUiEvent.ClearHistory)
                        showClearConfirmDialog = false
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Bottom Sheet Aktifkan Bluetooth
    if (showBluetoothSheet) {
        EnableBluetoothBottomSheet(
            sheetState = sheetState,
            onDismiss = onDismissSheet,
            onEnableClick = onEnableBluetoothClick
        )
    }
}

@Composable
fun HomeContent(
    state: BleUiState,
    onEvent: (BleUiEvent) -> Unit,
    onNavigateDetailDevice: (device: BleDeviceEntity) -> Unit
) {
    val filteredList = state.filteredLiveDevices
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RadarAnimationView(
                    modifier = Modifier.size(190.dp),
                    isScanning = state.isScanning,
                    devices = filteredList
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (state.isScanning) stringResource(R.string.scanning_in_progress) else stringResource(R.string.scan_paused),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (state.isScanning) MaterialTheme.colorScheme.primary else Color.Gray
                )

                Text(
                    text = stringResource(R.string.live_scanning_not_saved),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        item {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { onEvent(BleUiEvent.OnSearchQueryChanged(it)) },
                placeholderText = stringResource(R.string.search_placeholder_live)
            )
        }

        item {
            FilterSection(
                state = state,
                onEvent = onEvent
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.detected_devices_count, filteredList.size),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.sorted_by_signal),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.isScanning) stringResource(R.string.no_devices_detected) else stringResource(R.string.press_start_scan),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            items(filteredList, key = { it.macAddress }) { device ->
                LiveDeviceCard(
                    device = device,
                    onSaveClick = {
                        onEvent(BleUiEvent.SaveDeviceToHistory(device))
                    },
                    onNavigateDetailDevice = onNavigateDetailDevice
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    TrackingDeviceTheme {
        HomeContent(
            BleUiState(
                currentTab = 0,
                isScanning = true,
                liveDevices = listOf(
                    BleDeviceEntity("42:FE:8A:1B:90:02", "SmartWatch Pro", -30, 1.2),
                    BleDeviceEntity("11:22:33:44:55:66", "Headphones", -60, 3.8),
                    BleDeviceEntity("AA:BB:CC:DD:EE:FF", "Beacon Tag", -85, 8.5)
                )
            ),
            onEvent = { },
            onNavigateDetailDevice = { }
        )
    }
}
