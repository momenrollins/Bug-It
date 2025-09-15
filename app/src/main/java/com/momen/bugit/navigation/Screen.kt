package com.momen.bugit.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object BugForm : Screen("bug_form")
    object Success : Screen("success")
}
