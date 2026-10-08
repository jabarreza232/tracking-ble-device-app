package id.co.evolution.trackingdevice.presentation.screen

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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.co.evolution.trackingdevice.ui.theme.TrackingDeviceTheme
import kotlinx.coroutines.launch


data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "Pengajuan Reimbursement Mudah",
        description = "Kelola dan ajukan reimbursement operasional kantor Anda secara digital tanpa repot menyimpan nota fisik.",
        icon = Icons.Default.Edit,
    ),
    OnboardingPage(
        title = "Pantau Status Real-Time",
        description = "Lacak status persetujuan klaim Anda dari atasan dan divisi keuangan secara transparan dan cepat.",
        icon = Icons.Default.Lock,
    ),
    OnboardingPage(
        title = "Pencairan Cepat & Aman",
        description = "Proses verifikasi otomatis dan pencairan dana reimbursement langsung ke rekening Anda dengan aman.",
        icon = Icons.Default.CheckCircle,
    ),
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
) {
    val pagerState = rememberPagerState { onboardingPages.size }
    val coroutineScope = rememberCoroutineScope()

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
                TextButton(onClick = onFinishOnboarding) {
                    Text("Lewati")
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Pager Content (Slide)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            OnboardingPageItem(page = onboardingPages[page])
        }

        // Page Indicators (Dots)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(onboardingPages.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
                val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .height(8.dp)
                        .width(width),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Button: Lanjut atau Mulai
        Button(
            onClick = {
                if (pagerState.currentPage < (onboardingPages.size - 1)) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onFinishOnboarding()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        ) {
            Text(
                text = if (pagerState.currentPage == (onboardingPages.size - 1)) "Mulai Sekarang" else "Lanjut",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
fun OnboardingPageItem(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = page.title,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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