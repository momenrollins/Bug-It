package com.momen.bugit.data.model

data class Bug(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val imagePath: String = ""
)

data class ExtendedBug(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val imagePath: String = "",
    val priority: String = "Medium",
    val labels: String = "",
    val assignee: String = ""
)
