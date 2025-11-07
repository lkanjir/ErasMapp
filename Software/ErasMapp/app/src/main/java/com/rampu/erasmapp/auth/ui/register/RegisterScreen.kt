package com.rampu.erasmapp.auth.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rampu.erasmapp.R
import com.rampu.erasmapp.common.ui.LayoutTestPreview
import com.rampu.erasmapp.common.ui.components.LabeledInputField
import com.rampu.erasmapp.common.ui.components.Logo
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onEvent: (event: RegisterEvent) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
){
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
            text = stringResource(R.string.sign_up),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(30.dp))
        LabeledInputField(
            value = state.email,
            onValueChange = {
                onEvent(RegisterEvent.EmailChanged(it))
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
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        LabeledInputField(
            value = state.password,
            onValueChange = {
                onEvent(RegisterEvent.PasswordChanged(it))
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
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        LabeledInputField(
            value = state.confirmPassword,
            onValueChange = {
                onEvent(RegisterEvent.ConfirmPasswordChanged(it))
            },
            label = stringResource(R.string.confirm_password),
            modifier = Modifier.fillMaxWidth(0.8f),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = stringResource(R.string.confirm_password)
                )

            },
            placeholder = stringResource(R.string.confirm_password),
            isError = state.confirmPasswordError != null,
            supportingText = {
                if(state.confirmPasswordError != null) Text(text = state.confirmPasswordError)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth(0.8f),

            onClick = {
                onEvent(RegisterEvent.Submit)
            },
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

}

@LayoutTestPreview
@Composable
fun RegisterScreenPreview(){
    ErasMappTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ){
            RegisterScreen(
                state = RegisterUiState(),
                onEvent = {},
                contentPadding = WindowInsets.systemBars.asPaddingValues()
            )
        }

    }
}