package id.co.evolution.trackingdevice.presentation.screen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.co.evolution.trackingdevice.ui.theme.TrackingDeviceTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue


data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Pemindaian BLE Real-Time",
        description = "Deteksi perangkat Bluetooth Low Energy di sekitar Anda secara real-time dengan visualisasi radar dan pembaruan otomatis.",
        icon = Icons.Default.Bluetooth,
    ),
    OnboardingPage(
        title = "Monitor Kekuatan Sinyal",
        description = "Lacak RSSI dan estimasi jarak perangkat dengan kategori sinyal dari sangat kuat hingga lemah secara akurat.",
        icon = Icons.Default.SignalCellularAlt,
    ),
    OnboardingPage(
        title = "Riwayat Perangkat Tersimpan",
        description = "Simpan dan kelola riwayat semua perangkat BLE yang pernah terdeteksi dengan timestamp lengkap.",
        icon = Icons.Default.History,
    ),
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
) {
    val pagerState = rememberPagerState { onboardingPages.size }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        ) {
            // Top Bar: Tombol Skip / Lewati
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (pagerState.currentPage < (onboardingPages.size - 1)) {
                    TextButton(
                        onClick = onFinishOnboarding,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Lewati",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // Pager Content (Slide) with smooth animations
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                pageSpacing = 16.dp,
            ) { page ->
                OnboardingPageItem(
                    page = onboardingPages[page],
                    pagerState = pagerState,
                    currentPage = page
                )
            }

            // Page Indicators (Dots) - Animated
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(onboardingPages.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    
                    PageIndicator(
                        isSelected = isSelected,
                        index = iteration,
                        pagerState = pagerState
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Button: Lanjut atau Mulai - Elevated design
            Button(
                onClick = {
                    if (pagerState.currentPage < (onboardingPages.size - 1)) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                pagerState.currentPage + 1,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    } else {
                        onFinishOnboarding()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    text = if (pagerState.currentPage == (onboardingPages.size - 1)) "Mulai Sekarang" else "Lanjut",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PageIndicator(
    isSelected: Boolean,
    index: Int,
    pagerState: PagerState
) {
    val width by remember(pagerState.currentPage, pagerState.currentPageOffsetFraction) {
        derivedStateOf {
            val currentPage = pagerState.currentPage
            val offset = pagerState.currentPageOffsetFraction
            
            when {
                index == currentPage -> (24f - (offset.absoluteValue * 16f)).coerceAtLeast(8f)
                index == currentPage + 1 && offset > 0 -> 8f + (offset * 16f)
                index == currentPage - 1 && offset < 0 -> 8f + (offset.absoluteValue * 16f)
                else -> 8f
            }
        }
    }

    val color = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(CircleShape)
            .background(color)
            .height(8.dp)
            .width(width.dp),
    )
}

@Composable
fun OnboardingPageItem(
    page: OnboardingPage,
    pagerState: PagerState,
    currentPage: Int
) {
    // Calculate page offset for animations
    val pageOffset by remember(pagerState.currentPage, pagerState.currentPageOffsetFraction) {
        derivedStateOf {
            ((pagerState.currentPage - currentPage) + pagerState.currentPageOffsetFraction)
        }
    }

    // Scale animation based on scroll position
    val scale by remember(pageOffset) {
        derivedStateOf {
            1f - (pageOffset.absoluteValue * 0.1f).coerceIn(0f, 0.3f)
        }
    }

    // Alpha animation for fade effect
    val alpha by remember(pageOffset) {
        derivedStateOf {
            1f - (pageOffset.absoluteValue * 0.5f).coerceIn(0f, 1f)
        }
    }

    // Rotation for parallax effect
    val rotation by remember(pageOffset) {
        derivedStateOf {
            pageOffset * 10f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
                rotationY = rotation
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Animated Card Container for Icon
        Card(
            modifier = Modifier
                .size(160.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = page.title,
                    modifier = Modifier
                        .size(80.dp)
                        .graphicsLayer {
                            // Icon scales independently for emphasis
                            val iconScale = 1f + (1f - pageOffset.absoluteValue.coerceIn(0f, 1f)) * 0.1f
                            scaleX = iconScale
                            scaleY = iconScale
                        },
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title with elegant typography
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Description with subtle color
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.4f),
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    TrackingDeviceTheme() {
        OnboardingScreen {}
    }
}