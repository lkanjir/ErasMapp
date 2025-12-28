package com.rampu.erasmapp.channels.ui.channels

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.R
import com.rampu.erasmapp.common.ui.components.UserAvatar
import com.rampu.erasmapp.ui.theme.ErasMappTheme

data class ChannelIconOption(val key: String, val label: String, val resId: Int)

private val channelIconOptions = listOf(
    ChannelIconOption("general", "General", R.drawable.general),
    ChannelIconOption("dorms", "Dorms", R.drawable.dorms),
    ChannelIconOption("activities", "Activities", R.drawable.activity),
    ChannelIconOption("college", "College", R.drawable.college),
    ChannelIconOption("admin", "Admin", R.drawable.admin),
    ChannelIconOption("events", "Events", R.drawable.event),
    ChannelIconOption("health", "Health", R.drawable.health),
    ChannelIconOption("travel", "Travel", R.drawable.travel)
)

@Composable
fun channelIconForKey(iconKey: String?): ImageVector? {
    val resiId = channelIconOptions.firstOrNull { it.key == iconKey }?.resId ?: return null
    return ImageVector.vectorResource(resiId)
}

@Composable
fun ChannelIconPicker(
    selectedKey: String?,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        item {
            ChannelIconItem(
                label = "Auto",
                icon = null,
                isSelected = selectedKey == null,
                onclick = { onSelected(null) }
            )
        }
        items(channelIconOptions, key = { it.key }) { option ->
            ChannelIconItem(
                label = option.label,
                icon = ImageVector.vectorResource(option.resId),
                isSelected = selectedKey == option.key,
                onclick = { onSelected(option.key) }
            )
        }
    }
}

@Composable
private fun ChannelIconItem(
    label: String,
    icon: ImageVector?,
    isSelected: Boolean,
    onclick: () -> Unit
) {
    val color =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onclick)
            .padding(2.dp)
    ) {
        Box(modifier = Modifier.border(1.dp, color, CircleShape)) {
            UserAvatar(label = label, icon = icon)
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )

    }
}

@Preview
@Composable
fun ChannelIconPickerPreview() {
    ErasMappTheme {
        ChannelIconPicker(
            selectedKey = "general",
            onSelected = {},
        )
    }
}

@Preview
@Composable
fun ChannelIconItemPreview() {
    ErasMappTheme {
        ChannelIconItem(
            label = "Test",
            icon = null,
            isSelected = true,
            onclick = {}
        )
    }
}

@Preview
@Composable
fun ChannelIconItemPreview2() {
    ErasMappTheme {
        ChannelIconItem(
            label = "General",
            icon = channelIconForKey("general"),
            isSelected = false,
            onclick = {}
        )
    }
}
