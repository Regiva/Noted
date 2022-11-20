package com.noted.core.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class NotedStack(
    private val stackNavModel: StackNavModel,
) : StackScreen(stackNavModel) {

    constructor(rootScreen: Screen) : this(StackNavModel(rootScreen))

    @Composable
    override fun Content() {
        CompositionLocalProvider(LocalSnackbarHostState provides SnackbarHostState()) {
            TopScreenContent {
                SlideTransition()
            }
        }
    }
}

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}
