package com.rampu.erasmapp.auth.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEvent: (event: LoginEvent) -> Unit,
    onNavigateToRegister: () -> Unit
){

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Login screen")
        Button(
            onClick = onNavigateToRegister,
        ){
            Text("Register")
        }
    }
}