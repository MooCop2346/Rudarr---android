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
import com.ruddarr.tv.data.Media

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DetailScreen(media: Media) {
    Row(Modifier.fillMaxSize().padding(48.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        AsyncImage(
            model = media.posterUrl, contentDescription = media.title,
            modifier = Modifier.width(280.dp).aspectRatio(2f / 3f),
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("${media.title} (${media.year})")
            media.facts.forEach { Text(it) }
            media.overview?.let { Text(it) }
        }
    }
}
