package id.co.evolution.trackingdevice.domain.model

import androidx.compose.ui.graphics.Color

enum class SignalCategory(
    val label: String,
    val color: Color,
    val minRssi: Int,
    val maxRssi: Int
) {
    VERY_STRONG(
        label = "Sangat Kuat / Sangat Dekat",
        color = Color(0xFF2E7D32), // Green
        minRssi = -30,
        maxRssi = -10
    ),
    STRONG(
        label = "Kuat(Dekat)",
        color = Color(0xFF4CAF50), // Light Green
        minRssi = -50,
        maxRssi = -30
    ),
    MODERATE(
        label = "Cukup/Baik",
        color = Color(0xFFFFA000), // Amber
        minRssi = -70,
        maxRssi = -50
    ),
    WEAK(
        label = "Lemah",
        color = Color(0xFFFF5722), // Deep Orange
        minRssi = -80,
        maxRssi = -70
    ),
    VERY_WEAK(
        label = "Sangat Lemah / Putus-Putus",
        color = Color(0xFFD32F2F), // Red
        minRssi = -90,
        maxRssi = -80
    ),

    LOST_SIGNAL(
        label = "Sinyal Hilang (Lost)",
        color = Color(0xFF910000), // Red
        minRssi = -120,
        maxRssi = -90
    );

    companion object {
        fun fromRssi(rssi: Int): SignalCategory {
            return when {
                rssi >= -10 -> VERY_STRONG
                rssi >= -30 -> STRONG
                rssi >= -50 -> MODERATE
                rssi >= -70 -> WEAK
                rssi >= -80 -> VERY_WEAK
                else -> LOST_SIGNAL
            }
        }
    }
}
