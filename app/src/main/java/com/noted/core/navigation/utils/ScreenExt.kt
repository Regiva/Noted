package com.noted.core.navigation.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.stack.StackState

@Suppress("UNCHECKED_CAST")
val Screen.navContainer: NavigationContainer<StackState>
    @Composable get() = LocalContainerScreen.current as NavigationContainer<StackState>

val Screen.context: Context
    @Composable get() = LocalContext.current
