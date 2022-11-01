package com.noted.ui.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sort
import androidx.compose.ui.graphics.vector.ImageVector

object NotedIcons {
    val Add = Icons.Default.Add
    val Delete = Icons.Default.Delete
    val Save = Icons.Default.Save
    val Sort = Icons.Default.Sort
}

sealed class Icon {
    data class ImageVectorIcon(val imageVector: ImageVector) : Icon()
    data class DrawableResourceIcon(@DrawableRes val id: Int) : Icon()
}
