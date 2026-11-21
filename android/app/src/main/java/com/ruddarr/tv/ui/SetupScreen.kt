package com.ruddarr.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.ruddarr.tv.data.Instance

/** D-pad: focus a field and press select to open the TV keyboard (or use a phone's Google TV remote app). */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SetupScreen(initial: Instance?, onSave: (Instance) -> Unit) {
    var url by remember { mutableStateOf(initial?.url ?: "http://") }
    var key by remember { mutableStateOf(initial?.apiKey ?: "") }

    Column(
        Modifier.fillMaxSize().padding(48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Radarr Instance")
        OutlinedTextField(
            value = url, onValueChange = { url = it }, singleLine = true,
            label = { Text("URL (e.g. http://192.168.1.10:7878)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.width(600.dp),
        )
        OutlinedTextField(
            value = key, onValueChange = { key = it }, singleLine = true,
            label = { Text("API Key") },
            modifier = Modifier.width(600.dp),
        )
        Button(onClick = { onSave(Instance(url.trim(), key.trim())) }) { Text("Save") }
    }
}
