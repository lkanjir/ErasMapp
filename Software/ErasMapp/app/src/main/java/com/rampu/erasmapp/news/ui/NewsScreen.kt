package com.rampu.erasmapp.news.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun NewsScreen(
    onBack: () -> Unit,
    onEvent: (event: NewsEvent) -> Unit,
    state: NewsUiState,
){

}

@Composable
@Preview()
fun NewsScreenPreview(){
    ErasMappTheme {
        NewsScreen(
            onBack = {},
            onEvent = {},
            state = NewsUiState()
        )
    }
}
