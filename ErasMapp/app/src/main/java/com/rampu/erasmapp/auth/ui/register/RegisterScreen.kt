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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun RegisterScreen(
    state: RegisterUiState,
    onEvent: (event: RegisterEvent) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
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
            text = stringResource(R.string.sign_up),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(Modifier.height(30.dp))
        LabeledInputField(
            enabled = !state.isLoading,
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
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LabeledInputField(
            enabled = !state.isLoading,
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
            },
            visualTransformation = if(!passwordVisible) PasswordVisualTransformation()  else VisualTransformation.None,
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Icon(
                        painter = if (passwordVisible) painterResource(R.drawable.outline_visibility_off_24)
                        else painterResource(R.drawable.outline_visibility_24),
                        contentDescription = stringResource(R.string.password_visibility_icon)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LabeledInputField(
            enabled = !state.isLoading,
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
            },
            visualTransformation = if(!passwordVisible) PasswordVisualTransformation()  else VisualTransformation.None,
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Icon(
                        painter = if (passwordVisible) painterResource(R.drawable.outline_visibility_off_24)
                        else painterResource(R.drawable.outline_visibility_24),
                        contentDescription = stringResource(R.string.password_visibility_icon)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            enabled = !state.isLoading,
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
            if(state.isLoading){
                LoadingIndicator(modifier = Modifier.size(20.dp))
            }
            else{
                Text(
                    text = stringResource(R.string.sign_up),
                )
            }
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