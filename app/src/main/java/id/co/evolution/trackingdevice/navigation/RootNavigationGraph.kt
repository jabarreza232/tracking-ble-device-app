package id.co.evolution.trackingdevice.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import id.co.evolution.trackingdevice.domain.model.BleDeviceEntity
import id.co.evolution.trackingdevice.presentation.screen.DetailDeviceScreen
import id.co.evolution.trackingdevice.presentation.screen.HistoryDeviceScreen
import id.co.evolution.trackingdevice.presentation.screen.HomeScreen
import id.co.evolution.trackingdevice.presentation.screen.OnboardingScreen

@Composable
fun RootNavigationGraph(
    navController: NavHostController,
    isOnboardingCompleted: Boolean,
    onFinishOnboarding: () -> Unit
) {
    val startDestination = if (isOnboardingCompleted) NavScreen.Home.route else NavScreen.Onboarding.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = NavScreen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    onFinishOnboarding()
                    navController.navigate(NavScreen.Home.route) {
                        popUpTo(NavScreen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavScreen.DetailDevice.route,
            arguments = listOf(
                navArgument("macAddress") {
                    type = NavType.StringType
                }
            )
        ) {

            val device = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<BleDeviceEntity>("selected_device")

            if (device != null) {
                DetailDeviceScreen(
                    device = device,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                // Tampilan fallback jika data tidak sengaja hilang
                Text("Data perangkat tidak ditemukan")
            }
        }


        composable(route = NavScreen.Home.route) {
            HomeScreen(
                onNavigateDetailDevice = {

                    navController.currentBackStackEntry?.savedStateHandle?.set("selected_device", it)
                    navController.navigate(NavScreen.DetailDevice.route)
                }
            )
        }
    }
}
