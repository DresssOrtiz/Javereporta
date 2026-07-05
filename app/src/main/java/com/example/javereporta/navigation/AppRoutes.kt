package com.example.javereporta.navigation

object AppRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME = "home"
    const val CREATE_REPORT = "create_report"
    const val REPORTS_LIST = "reports_list"
    const val REPORT_DETAIL = "report_detail"
    const val PROFILE = "profile"
    const val CAMPUS_MAP = "campus_map"

    const val REPORT_ID_KEY = "reportId"
    const val REPORT_DETAIL_ROUTE = "$REPORT_DETAIL/{$REPORT_ID_KEY}"
}
