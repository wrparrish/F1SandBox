
object Routes {
    object Home {
        const val GRAPH = "home_graph"
        const val SCREEN = "home_screen"
    }

    object MeetingDetails {
        const val SCREEN = "meeting_screen/{meetingId}"
        const val ARG_MEETING_ID = "meetingId"
        fun createRoute(meetingId: Int): String {
            return "meeting_screen/$meetingId"
        }
    }

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