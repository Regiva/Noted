package com.noted.ui.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.ui.graphics.vector.ImageVector

object NotedIcons {
    val Add = Icons.Default.Add
    val AddAlert = Icons.Default.AddAlert
    val Delete = Icons.Default.Delete
    val Save = Icons.Default.Save
    val Sort = Icons.Filled.Sort

    object Outlined {
        val AddAlert = Icons.Outlined.AddAlert
    }
}

sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}
