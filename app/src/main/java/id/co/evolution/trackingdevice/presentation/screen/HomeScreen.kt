package id.co.evolution.trackingdevice.presentation.screen

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.co.evolution.trackingdevice.component.EnableBluetoothBottomSheet
import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.domain.model.SignalCategory
import id.co.evolution.trackingdevice.presentation.component.HistoryDeviceCard
import id.co.evolution.trackingdevice.presentation.component.LiveDeviceCard
import id.co.evolution.trackingdevice.presentation.component.RadarAnimationView
import id.co.evolution.trackingdevice.presentation.component.SearchBar
import id.co.evolution.trackingdevice.presentation.component.SignalCategoryItem
import id.co.evolution.trackingdevice.presentation.state.BleUiEvent
import id.co.evolution.trackingdevice.presentation.state.BleUiState
import id.co.evolution.trackingdevice.presentation.viewmodel.BleViewModel
import id.co.evolution.trackingdevice.ui.theme.TrackingDeviceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BleViewModel = hiltViewModel(),
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
            viewModel.onEvent(BleUiEvent.OnError("Izin Bluetooth / Lokasi diperlukan untuk scan."))
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
    onNavigateDetailDevice: (device: BleDeviceEntity)-> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Home", "Riwayat")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BLE Tracking Device",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (state.currentTab == 1 && state.historyDevices.isNotEmpty()) {
                        IconButton(onClick = { showClearConfirmDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Hapus Semua Riwayat",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // FAB hanya ditampilkan pada Tab Home
            if (state.currentTab == 0) {
                if (state.isScanning) {
                    ExtendedFloatingActionButton(
                        onClick = onStopScanClick,
                        icon = { Icon(Icons.Default.Stop, contentDescription = "Stop") },
                        text = { Text("Hentikan Scan") },
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    ExtendedFloatingActionButton(
                        onClick = onStartScanClick,
                        icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Start") },
                        text = { Text("Mulai Scan") },
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
            // Tab Menu: Home & Riwayat
            PrimaryTabRow(
                selectedTabIndex = state.currentTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = state.currentTab == index,
                        onClick = { onEvent(BleUiEvent.OnTabChanged(index)) },
                        text = {
                            Text(
                                text = if (index == 0) "$title (${state.liveDevices.size})"
                                else "$title (${state.historyDevices.size})",
                                fontWeight = if (state.currentTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            if (index == 0) {
                                Icon(Icons.Default.Radar, contentDescription = "Home Radar")
                            } else {
                                Icon(Icons.Default.History, contentDescription = "Riwayat")
                            }
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
            when (state.currentTab) {
                0 -> HomeContent(
                    state = state,
                    onEvent = onEvent,
                    onNavigateDetailDevice = onNavigateDetailDevice
                )
                1 -> HistoryDeviceScreen(
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
            title = { Text("Hapus Semua Riwayat") },
            text = { Text("Apakah Anda yakin ingin menghapus seluruh riwayat perangkat yang terdeteksi?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEvent(BleUiEvent.ClearHistory)
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Batal")
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
    onNavigateDetailDevice:(device: BleDeviceEntity)-> Unit
) {
    val filteredList = state.filteredLiveDevices
    val categoryList = state.signalCategoryList
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Radar Animation Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RadarAnimationView(
                    modifier = Modifier.size(190.dp),
                    isScanning = state.isScanning
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (state.isScanning) "Memindai perangkat sekitar..." else "Scan dijeda",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (state.isScanning) MaterialTheme.colorScheme.primary else Color.Gray
                )

                Text(
                    text = "Live scanning (tidak disimpan ke lokal)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // 2. Search Bar
        item {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { onEvent(BleUiEvent.OnSearchQueryChanged(it)) },
                placeholderText = "Cari perangkat live (nama / MAC)..."
            )
        }
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp) // Sesuaikan dengan padding LazyColumn Anda
            ) {
                items(categoryList, key = { it.name }) { category ->
                    SignalCategoryItem(category)
                }
            }
        }
        // 3. Status Urutan Otomatis Berdasarkan Sinyal Terkuat
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Perangkat Terdeteksi (${filteredList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Diurutkan sinyal terkuat",
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
                        text = if (state.isScanning) "Belum ada perangkat terdeteksi..." else "Tekan 'Mulai Scan' untuk mencari",
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
            BleUiState(currentTab = 0),
            {  },
            {  },
            {  },
            false,
            {  },
            {  },
            {  }

        )
    }
}