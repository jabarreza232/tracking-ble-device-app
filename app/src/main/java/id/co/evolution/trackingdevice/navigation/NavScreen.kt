package id.co.evolution.trackingdevice.navigation

sealed class NavScreen(val route:String) {
    object DetailDevice : NavScreen("detail_device/{macAddress}") {
        // Helper function untuk membuat route dengan parameter konkret
        fun createRoute(macAddress: String) = "detail_device/$macAddress"
    }
    object HistoryDevice: NavScreen("history_device_screen")
    object Onboarding: NavScreen("onboarding_screen")
    object Home: NavScreen("home_screen")
}