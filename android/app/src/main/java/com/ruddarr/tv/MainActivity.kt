package com.ruddarr.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.ruddarr.tv.data.InstanceStore
import com.ruddarr.tv.data.Movie
import com.ruddarr.tv.ui.DetailScreen
import com.ruddarr.tv.ui.LibraryScreen
import com.ruddarr.tv.ui.SetupScreen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) { App() }
        }
    }
}

@Composable
private fun App() {
    val store = InstanceStore(LocalContext.current)
    val nav = rememberNavController()
    var instance by remember { mutableStateOf(store.load()) }
    var selected by remember { mutableStateOf<Movie?>(null) }

    NavHost(nav, startDestination = if (instance == null) "setup" else "library") {
        composable("setup") {
            SetupScreen(initial = instance) {
                store.save(it)
                instance = it
                nav.navigate("library") { popUpTo("setup") { inclusive = true } }
            }
        }
        composable("library") {
            LibraryScreen(
                instance = instance!!,
                onOpen = { selected = it; nav.navigate("detail") },
                onSettings = { nav.navigate("setup") },
            )
        }
        composable("detail") {
            selected?.let { DetailScreen(it) }
        }
    }
}
