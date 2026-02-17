package com.rampu.erasmapp.auth.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.R
import com.rampu.erasmapp.common.ui.LayoutTestPreview
import com.rampu.erasmapp.common.ui.components.LabeledInputField
import com.rampu.erasmapp.common.ui.components.LoadingIndicator
import com.rampu.erasmapp.common.ui.components.Logo
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEvent: (event: LoginEvent) -> Unit,
    onNavigateToRegister: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    onGoogleSignIn: () -> Unit
){

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Logo(
            modifier = Modifier
                .size(150.dp)
        )

        Spacer(Modifier.height(30.dp))

        Text(
            text = stringResource(R.string.sign_in_to_your_account),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(30.dp))

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
            isError = state.emailError != null,
            supportingText = {
                if(state.emailError != null) Text(text = state.emailError)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !state.isLoading
        )

        Spacer(Modifier.height(10.dp))

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
            isError = state.passwordError != null,
            supportingText = {
                if(state.passwordError != null) Text(text = state.passwordError)
            },
            visualTransformation = if(!passwordVisible) PasswordVisualTransformation()  else VisualTransformation.None,
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ){
                    Icon(
                        painter = if(passwordVisible) painterResource(R.drawable.outline_visibility_off_24)
                        else painterResource(R.drawable.outline_visibility_24),
                        contentDescription = stringResource(R.string.password_visibility_icon)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !state.isLoading
        )

        Spacer(Modifier.height(10.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(0.8f),

            onClick = {
                onEvent(LoginEvent.Submit)
            },
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(vertical = 15.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp
            ),
            enabled = !state.isLoading
        ) {
            if(state.isLoading){
                LoadingIndicator(modifier = Modifier.size(20.dp))
            }
            else{
                Text(
                    text = stringResource(R.string.sign_in),
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.don_t_have_an_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            TextButton(
                onClick = onNavigateToRegister,
                enabled = !state.isLoading
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically,
        ){
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
            Text(
                text = "OR",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }

        Spacer(Modifier.height(30.dp))

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth(0.8f),

            onClick = onGoogleSignIn,
            shape = RoundedCornerShape(5.dp),
            contentPadding = PaddingValues(vertical = 15.dp),
            enabled = !state.isLoading
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Icon(
                    painter = painterResource(R.drawable.google_logo),
                    contentDescription = stringResource(R.string.sign_in_with_google),
                    tint = Color.Unspecified,
                    modifier = Modifier.requiredSize(20.dp)
                )

                Spacer(modifier = Modifier.width(15.dp))

                Text(
                    text = stringResource(R.string.sign_in_with_google),
                )
            }

        }
    }
}

@LayoutTestPreview
@Composable
fun LoginScreenPreview(){
        ErasMappTheme {
            Surface(
                color = MaterialTheme.colorScheme.background
            ){
                LoginScreen(
                    state = LoginUiState(),
                    onEvent = {},
                    onNavigateToRegister = {},
                    contentPadding = WindowInsets.systemBars.asPaddingValues(),
                    onGoogleSignIn = {}
                )
            }
        }
}