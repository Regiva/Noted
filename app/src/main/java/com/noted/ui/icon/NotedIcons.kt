package com.noted.ui.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object NotedIcons {
    val Add = Icons.Default.Add
    val Delete = Icons.Default.Delete
    val Save = Icons.Default.Save
    val Sort = Icons.Default.Sort
    val AddAlert = Icons.Default.AddAlert
}

sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}
