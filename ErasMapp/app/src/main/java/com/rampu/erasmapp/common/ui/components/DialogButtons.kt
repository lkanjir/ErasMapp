package com.rampu.erasmapp.common.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun DialogConfirmButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    TextButton(onClick = onClick, enabled = enabled) {
        Text(text)
    }
}

@Composable
fun DialogDismissButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    TextButton(onClick = onClick, enabled = enabled) {
        Text(text)
    }
}


@Composable
@Preview(showBackground = true, widthDp = 500)
fun DialogConfirmButtonPreview(){
    ErasMappTheme {
        DialogConfirmButton(
            text = "Test",
            onClick = {},
            enabled = false
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 500)
fun DialogDismissButtonPreview(){
    ErasMappTheme {
        DialogDismissButton(
            text = "Dismiss",
            onClick = {},
            enabled = true
        )
    }
}