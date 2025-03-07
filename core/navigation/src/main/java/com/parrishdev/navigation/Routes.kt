
object Routes {
    const val HOME_GRAPH = "home_graph"
    const val HOME_SCREEN = "home_screen"

    object Drivers {
        const val GRAPH = "drivers_graph"
        const val SCREEN = "drivers_screen"
    }

    object DriverDetails {
        const val SCREEN = "driver_details_screen/{driverId}"
        const val ARG_DRIVER_ID = "driverId"
        fun createRoute(driverId: String): String {
            return "driver_details_screen/$driverId"
        }
    }

    object Settings {
        const val GRAPH = "settings_graph"
        const val SCREEN = "settings_screen"
    }
}