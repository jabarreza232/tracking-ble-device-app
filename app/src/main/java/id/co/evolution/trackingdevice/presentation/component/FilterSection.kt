package id.co.evolution.trackingdevice.presentation.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.co.evolution.trackingdevice.domain.model.SignalCategory
import id.co.evolution.trackingdevice.presentation.state.BleUiEvent
import id.co.evolution.trackingdevice.presentation.state.BleUiState

@Composable
fun FilterSection(
    state: BleUiState,
    onEvent: (BleUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val rssiOptions = listOf(
        null to "Semua RSSI",
        -50 to "≥ -50 dBm (Dekat)",
        -70 to "≥ -70 dBm (Sedang)",
        -90 to "≥ -90 dBm (Jauh)"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Header Filter & Reset Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Filter Perangkat",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (state.selectedCategory != null || state.minRssiFilter != null) {
                InputChip(
                    selected = true,
                    onClick = { onEvent(BleUiEvent.ResetFilters) },
                    label = { Text("Reset Filter", style = MaterialTheme.typography.labelSmall) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                )
            }
        }

        // Filter 1: Kategori Sinyal (Signal Category)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = state.selectedCategory == null,
                onClick = { onEvent(BleUiEvent.OnCategoryFilterSelected(null)) },
                label = { Text("Semua Sinyal", style = MaterialTheme.typography.labelSmall) }
            )

            SignalCategory.entries.forEach { category ->
                val isSelected = state.selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newCategory = if (isSelected) null else category
                        onEvent(BleUiEvent.OnCategoryFilterSelected(newCategory))
                    },
                    label = {
                        Text(
                            text = category.label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = category.color.copy(alpha = 0.25f),
                        selectedLabelColor = category.color
                    )
                )
            }
        }

        // Filter 2: Batas Minimal RSSI
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            rssiOptions.forEach { (minRssi, label) ->
                val isSelected = state.minRssiFilter == minRssi
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newRssi = if (isSelected) null else minRssi
                        onEvent(BleUiEvent.OnMinRssiFilterSelected(newRssi))
                    },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
            }
        }
    }
}
