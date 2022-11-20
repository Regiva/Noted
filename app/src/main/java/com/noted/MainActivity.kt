package com.noted

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.Screen
import com.noted.core.navigation.NotedStack
import com.noted.features.note.presentation.note.NotesScreen
import com.noted.ui.theme.NotedTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var rootScreen: Screen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootScreen = Modo.init(savedInstanceState, rootScreen) {
            NotedStack(NotesScreen())
        }
        setContent {
            NotedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    rootScreen?.Content()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        Modo.save(outState, rootScreen)
        super.onSaveInstanceState(outState, outPersistentState)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NotedTheme {

    }
}