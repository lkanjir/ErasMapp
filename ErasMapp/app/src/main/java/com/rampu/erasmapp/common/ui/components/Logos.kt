package com.rampu.erasmapp.common.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.rampu.erasmapp.R
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun Logo(
    modifier: Modifier = Modifier,
    color: Color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
) {
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.logo_black),
        contentDescription = "logo",
        tint = color
    )
}


@Preview
@Composable
fun LogoPreview() {
    ErasMappTheme {
        Logo()
    }
}