package id.co.evolution.trackingdevice.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.domain.model.BleDeviceHistoryEntity
import id.co.evolution.trackingdevice.domain.model.SignalCategory
import id.co.evolution.trackingdevice.ui.theme.TrackingDeviceTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LiveDeviceCard(
    device: BleDeviceEntity,
    modifier: Modifier = Modifier,
    onSaveClick: (() -> Unit)? = null,
    onNavigateDetailDevice: (device: BleDeviceEntity)->Unit
) {
    val category = device.signalCategory

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = {onNavigateDetailDevice(device)}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Bluetooth dengan lingkaran latar belakang
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    tint = category.color
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = device.deviceName ?: "Unknown Device",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Text(
                    text = device.macAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column (
                    verticalArrangement = Arrangement.Center
                ) {
                    // Badge Kategori Sinyal
                    SignalBadge(category = category)

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${device.rawRssi} dBm",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = category.color
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "• ~${String.format(Locale.getDefault(), "%.1f", device.estimatedDistance)} m",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

//            if (onSaveClick != null) {
//                IconButton(onClick = onSaveClick) {
//                    Icon(
//                        imageVector = Icons.Default.BookmarkAdd,
//                        contentDescription = "Simpan ke Riwayat",
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
        }
    }
}

@Composable
fun HistoryDeviceCard(
    history: BleDeviceHistoryEntity,
    modifier: Modifier = Modifier,
    onDeleteClick: (() -> Unit)? = null,
    onNavigateDetailDevice: ((device: BleDeviceEntity) -> Unit)? = null
) {
    val category = SignalCategory.fromRssi(history.rawRssi)
    val formattedTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        .format(Date(history.timestamp))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = {
            onNavigateDetailDevice?.invoke(
                BleDeviceEntity(
                    macAddress = history.macAddress,
                    deviceName = history.deviceName,
                    rawRssi = history.rawRssi,
                    estimatedDistance = history.estimatedDistance,
                    lastSeenTimestamp = history.timestamp
                )
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SignalCellularAlt,
                    contentDescription = null,
                    tint = category.color
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.deviceName ?: "Unknown Device",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = history.macAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column {
                    SignalBadge(category = category)

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${history.rawRssi} dBm",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = category.color
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Terdeteksi: $formattedTime",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            if (onDeleteClick != null) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Riwayat",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun SignalBadge(category: SignalCategory) {

    Surface(
        color = category.color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = category.label,
            color = category.color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}



@Preview(showBackground = true)
@Composable
fun LiveDeviceCardPreview() {
    TrackingDeviceTheme {
        LiveDeviceCard(BleDeviceEntity("",null,0,0.0,0), Modifier.fillMaxWidth(),{},{})
    }
}
@Preview(showBackground = true)
@Composable
fun HistoryDeviceCardPreview() {
    TrackingDeviceTheme {
        HistoryDeviceCard(BleDeviceHistoryEntity(1,"","Unknown",0,0.0,"Lemah",0), Modifier.fillMaxWidth(),{})
    }
}
@Preview(showBackground = true)
@Composable
fun SignalBadgePreview() {
    TrackingDeviceTheme {
        SignalBadge(SignalCategory.VERY_STRONG)
    }
}