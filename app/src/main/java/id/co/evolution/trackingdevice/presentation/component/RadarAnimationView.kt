package id.co.evolution.trackingdevice.presentation.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarAnimationView(
    modifier: Modifier = Modifier,
    isScanning: Boolean = false,
    radarColor: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarTransition")

    // Sweep rotation angle (0 to 360)
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarSweep"
    )

    // Pulse ripple effect
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarPulse"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = minOf(size.width, size.height) / 2f * 0.9f

            // 1. Gambar lingkaran-lingkaran konsentris (Radar Grid)
            val ringCount = 4
            for (i in 1..ringCount) {
                val radius = maxRadius * (i.toFloat() / ringCount)
                drawCircle(
                    color = radarColor.copy(alpha = 0.2f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // 2. Garis sumbu silang (Crosshairs)
            drawLine(
                color = radarColor.copy(alpha = 0.15f),
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = radarColor.copy(alpha = 0.15f),
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1.dp.toPx()
            )

            if (isScanning) {
                // 3. Pulse wave ripple
                val pulseRadius = maxRadius * pulseProgress
                val pulseAlpha = (1f - pulseProgress).coerceIn(0f, 1f) * 0.45f
                drawCircle(
                    color = radarColor.copy(alpha = pulseAlpha),
                    radius = pulseRadius,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )

                // 4. Sweeping line & beam arc
                val radians = Math.toRadians(sweepAngle.toDouble())
                val sweepEnd = Offset(
                    x = center.x + (maxRadius * cos(radians)).toFloat(),
                    y = center.y + (maxRadius * sin(radians)).toFloat()
                )

                // Garis sapuan radar
                drawLine(
                    color = radarColor.copy(alpha = 0.8f),
                    start = center,
                    end = sweepEnd,
                    strokeWidth = 2.5.dp.toPx()
                )

                // Arc gradien untuk efek fading radar beam
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            radarColor.copy(alpha = 0.25f)
                        ),
                        center = center
                    ),
                    startAngle = sweepAngle - 50f,
                    sweepAngle = 50f,
                    useCenter = true,
                    topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                    size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
                )
            }

            // Titik tengah radar (blip pusat)
            drawCircle(
                color = radarColor,
                radius = 4.dp.toPx(),
                center = center
            )
        }
    }
}
