package com.rampu.erasmapp.news.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.channels.ui.channels.channelIconForKey
import com.rampu.erasmapp.channels.ui.channels.channelIconOptionsList
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun NewsCategoryFilter(
    selectedTopic: String?,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            FilterChip(
                selected = selectedTopic.isNullOrBlank(),
                onClick = { onSelected(null) }, label = { Text("All") })
        }
        items(channelIconOptionsList(), key = { it.key }) { option ->
            FilterChip(
                selected = selectedTopic == option.key,
                onClick = { onSelected(option.key) },
                label = { Text(option.label) },
                leadingIcon = {
                    channelIconForKey(option.key)?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null
                        )
                    }
                })
        }
    }

}

@Preview(showBackground = true)
@Composable
fun NewsCategoryFilterPreview() {
    ErasMappTheme {
        NewsCategoryFilter(
            selectedTopic = "this",
            onSelected = {},
        )
    }
}