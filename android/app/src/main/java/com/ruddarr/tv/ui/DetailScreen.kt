package com.ruddarr.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.ruddarr.tv.data.Movie

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DetailScreen(movie: Movie) {
    Row(Modifier.fillMaxSize().padding(48.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        AsyncImage(
            model = movie.posterUrl, contentDescription = movie.title,
            modifier = Modifier.width(280.dp).aspectRatio(2f / 3f),
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("${movie.title} (${movie.year})")
            Text(listOfNotNull(
                movie.certification, if (movie.runtime > 0) "${movie.runtime} min" else null,
                movie.genres.takeIf { it.isNotEmpty() }?.joinToString(", "),
            ).joinToString(" · "))
            Text(if (movie.hasFile) "Downloaded" else if (movie.monitored) "Missing (monitored)" else "Unmonitored")
            movie.overview?.let { Text(it) }
        }
    }
}
