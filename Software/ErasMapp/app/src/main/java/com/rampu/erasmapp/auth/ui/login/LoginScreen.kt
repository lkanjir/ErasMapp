package com.rampu.erasmapp.auth.ui.login

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.R
import com.rampu.erasmapp.common.ui.components.LabeledInputField
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEvent: (event: LoginEvent) -> Unit,
    onNavigateToRegister: () -> Unit
){
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LabeledInputField(
                value = state.email,
                onValueChange = {
                    onEvent(LoginEvent.EmailChanged(it))
                },
                label = stringResource(R.string.email),
                modifier = Modifier.fillMaxWidth(0.8f),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = stringResource(R.string.email)
                    )

                },
                placeholder = stringResource(R.string.email),
                isError = false
            )

            Spacer(modifier = Modifier.height(30.dp))

            LabeledInputField(
                value = state.password,
                onValueChange = {
                    onEvent(LoginEvent.PasswordChanged(it))
                },
                label = stringResource(R.string.password),
                modifier = Modifier.fillMaxWidth(0.8f),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = stringResource(R.string.password)
                    )

                },
                placeholder = stringResource(R.string.password),
                isError = false
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth(0.8f),

                onClick = {
                    onEvent(LoginEvent.Submit)
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(5.dp),
                contentPadding = PaddingValues(vertical = 15.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 5.dp
                )
            ) {
                Text(
                    text = stringResource(R.string.sign_in),
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don’t have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun LoginScreenPreview(){
        ErasMappTheme {
            LoginScreen(
                state = LoginUiState(),
                onEvent = {}
            ) { }
        }



}