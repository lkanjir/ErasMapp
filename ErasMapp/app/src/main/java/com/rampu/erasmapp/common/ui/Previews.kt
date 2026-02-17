package com.rampu.erasmapp.common.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "XS - Nexus One", device = "id:pixel_5", showSystemUi = true, showBackground = true)
@Preview(name = "S - Pixel 4", device = "id:pixel_4", showSystemUi = true, showBackground = true)
@Preview(name = "M - Pixel 7", device = "id:pixel_7", showSystemUi = true, showBackground = true)
@Preview(name = "L - Pixel 7 Pro", device = "id:pixel_7_pro", showSystemUi = true, showBackground = true)
@Preview(name = "XL - Pixel Fold (folded)", device = "id:pixel_fold", showSystemUi = true, showBackground = true)
annotation class PhoneSizePreviews

@Preview(name = "M - Pixel 7", device = "id:pixel_7", showSystemUi = true, showBackground = true)
annotation class StandardPreview
@Preview(
    name = "Dark - Pixel 7",
    device = "id:pixel_7",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
    showBackground = true
)
annotation class DarkModePreview

@Preview(
    name = "Tablet - Pixel C",
    device = "id:pixel_c",
    showSystemUi = true,
    showBackground = true
)
annotation class TabletPreview

@TabletPreview
@DarkModePreview
@PhoneSizePreviews
annotation class LayoutTestPreview