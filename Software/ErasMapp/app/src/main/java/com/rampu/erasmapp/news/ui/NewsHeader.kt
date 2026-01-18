package com.rampu.erasmapp.news.ui

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun NewsHeader(showAdd: Boolean, addEnabled: Boolean, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "News",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium)
        )
        if(showAdd){
            IconButton(onClick = onAdd, enabled = addEnabled) {
                Icon(Icons.Filled.Add, contentDescription = "Add news")
            }
        }
    }

}

@Composable
@Preview
fun NewsHeaderPreview() {
    ErasMappTheme {
        NewsHeader(
            showAdd = true,
            addEnabled = true,
            onAdd = {}
        )
    }
}

@Composable
@Preview
fun NewsHeaderPreview2() {
    ErasMappTheme {
        NewsHeader(
            showAdd = false,
            addEnabled = true,
            onAdd = {}
        )
    }
}