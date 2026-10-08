package id.co.evolution.trackingdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import id.co.evolution.trackingdevice.navigation.RootNavigationGraph
import id.co.evolution.trackingdevice.presentation.viewmodel.MainViewModel
import id.co.evolution.trackingdevice.ui.theme.TrackingDeviceTheme
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackingDeviceTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

                    RootNavigationGraph(
                        navController = navController,
                        isOnboardingCompleted = isOnboardingCompleted,
                        onFinishOnboarding = { mainViewModel.finishOnboarding() }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrackingDeviceTheme {
        Greeting("Android")
    }
}