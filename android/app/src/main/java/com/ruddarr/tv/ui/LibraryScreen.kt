package com.ruddarr.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.ruddarr.tv.data.Instance
import com.ruddarr.tv.data.Movie
import com.ruddarr.tv.data.RadarrApi

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LibraryScreen(instance: Instance, onOpen: (Movie) -> Unit, onSettings: () -> Unit) {
    var movies by remember { mutableStateOf<List<Movie>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(instance) {
        try {
            movies = RadarrApi(instance).movies().sortedBy { it.sortTitle }
            error = null
        } catch (e: Exception) {
            error = e.message ?: "Request failed"
        }
    }

    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Movies${movies?.let { " (${it.size})" } ?: ""}")
            Button(onClick = onSettings) { Text("Settings") }
        }

        when {
            error != null -> Text("Error: $error")
            movies == null -> Text("Loading…")
            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(160.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(movies!!, key = { it.id }) { movie ->
                    Card(onClick = { onOpen(movie) }) {
                        Box(Modifier.aspectRatio(2f / 3f)) {
                            AsyncImage(
                                model = movie.posterUrl, contentDescription = movie.title,
                                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}
