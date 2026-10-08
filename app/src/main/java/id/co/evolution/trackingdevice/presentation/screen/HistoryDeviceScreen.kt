package id.co.evolution.trackingdevice.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.presentation.component.HistoryDeviceCard
import id.co.evolution.trackingdevice.presentation.component.SearchBar
import id.co.evolution.trackingdevice.presentation.state.BleUiEvent
import id.co.evolution.trackingdevice.presentation.state.BleUiState


@Composable
fun HistoryDeviceScreen(
    state: BleUiState,
    onEvent: (BleUiEvent) -> Unit,
    onNavigateDetailDevice: (device: BleDeviceEntity) -> Unit = {}
) {
    val filteredList = state.filteredHistoryDevices

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { onEvent(BleUiEvent.OnSearchQueryChanged(it)) },
                placeholderText = "Cari di riwayat (nama / MAC)..."
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
                    text = "Riwayat Tersimpan (${filteredList.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sinyal terkuat",
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
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.searchQuery.isNotEmpty()) "Tidak ada hasil pencarian riwayat"
                        else "Belum ada riwayat tersimpan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            items(filteredList, key = { it.id }) { historyItem ->
                HistoryDeviceCard(
                    history = historyItem,
                    onDeleteClick = {
                        onEvent(BleUiEvent.DeleteHistoryItem(historyItem.id))
                    },
                    onNavigateDetailDevice = onNavigateDetailDevice
                )
            }
        }
    }
}
