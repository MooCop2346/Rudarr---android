package com.ruddarr.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
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
import com.ruddarr.tv.data.Kind
import com.ruddarr.tv.data.Media
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
    var kind by remember { mutableStateOf(Kind.Radarr) }
    var selected by remember { mutableStateOf<Media?>(null) }
    // bumped after saving so screens re-read the stored instance
    var version by remember { mutableIntStateOf(0) }

    fun start(): String = if (store.load(Kind.Radarr) == null && store.load(Kind.Sonarr) == null) "setup" else "library"

    NavHost(nav, startDestination = start()) {
        composable("setup") {
            SetupScreen(kind = kind, initial = store.load(kind)) {
                store.save(kind, it)
                version++
                nav.navigate("library") { popUpTo("setup") { inclusive = true } }
            }
        }
        composable("library") {
            val instance = remember(kind, version) { store.load(kind) }
            if (instance == null) {
                // chosen kind isn't configured yet
                LaunchedEffect(kind) { nav.navigate("setup") }
            } else {
                LibraryScreen(
                    kind = kind,
                    instance = instance,
                    onKind = { kind = it },
                    onOpen = { selected = it; nav.navigate("detail") },
                    onSettings = { nav.navigate("setup") },
                )
            }
        }
        composable("detail") {
            selected?.let { DetailScreen(it) }
        }
    }
}
