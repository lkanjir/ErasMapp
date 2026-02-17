package com.rampu.erasmapp.auth.ui

import android.app.Activity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.rampu.erasmapp.R
import com.rampu.erasmapp.auth.ui.login.LoginEffect
import com.rampu.erasmapp.auth.ui.login.LoginScreen
import com.rampu.erasmapp.auth.ui.login.LoginViewModel
import com.rampu.erasmapp.auth.ui.register.RegisterEffect
import com.rampu.erasmapp.auth.ui.register.RegisterScreen
import com.rampu.erasmapp.auth.ui.register.RegisterViewModel
import com.rampu.erasmapp.common.util.generateSecureRandomNonce
import com.rampu.erasmapp.ui.theme.ErasMappTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthGraph(){
    val navController = rememberNavController()
    val snackbarHostState = remember{ SnackbarHostState() }

    ErasMappTheme {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = LoginRoute,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
            ){
                composable<LoginRoute> {
                    val vm: LoginViewModel = koinViewModel()
                    val state = vm.uiState.collectAsStateWithLifecycle()

                    val context = LocalContext.current
                    val defaultWebClientId = stringResource(R.string.default_web_client_id)
                    val scope = rememberCoroutineScope()
                    val credentialManager = remember { CredentialManager.create(context) }

                    suspend fun parseCredentialResponse(credential: Credential){
                        try{
                            val cred = credential as? CustomCredential
                            if(cred?.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                                val googleCredential = GoogleIdTokenCredential.createFrom(cred.data)
                                val idToken = googleCredential.idToken
                                if (idToken.isEmpty()) {
                                    vm.effect.emit(LoginEffect.ShowError("Google sign-in failed: empty token"))
                                } else {
                                    vm.signInWithGoogle(idToken)
                                }
                            }
                            else {
                                vm.effect.emit(LoginEffect.ShowError("Unsupported credential type"))
                            }
                        } catch (_: GoogleIdTokenParsingException) {
                            vm.effect.emit(LoginEffect.ShowError("Failed to parse Google credential"))
                        }
                    }

                    fun onGoogleSignInClick(){
                        scope.launch {
                            try{
                                val idOption = GetGoogleIdOption.Builder()
                                    .setServerClientId(defaultWebClientId)
                                    .setFilterByAuthorizedAccounts(false)
                                    .setNonce(generateSecureRandomNonce())
                                    .build()

                                val request = GetCredentialRequest(listOf(idOption))
                                val activity = context as? Activity
                                    ?: run {
                                        vm.effect.emit(LoginEffect.ShowError("Internal error: no activity"))
                                        return@launch
                                    }
                                val response = credentialManager.getCredential(context = activity, request = request)
                                parseCredentialResponse(response.credential)
                            } catch (e: GetCredentialException){
                                val msg = when (e) {
                                    is NoCredentialException -> "No Google account available."
                                    is GetCredentialInterruptedException -> "Sign-in interrupted. Try again."
                                    is GetCredentialCancellationException -> "Sign-in cancelled."
                                    else -> "Google sign-in unavailable. Try again."
                                }
                                vm.effect.emit(LoginEffect.ShowError(msg))
                            }
                            catch (_: Throwable){
                                vm.effect.emit(LoginEffect.ShowError("Google sign-in failed. Try again."))
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
                        vm.effect.collect { event ->
                            when(event){
                                LoginEffect.NavigateHome -> Unit
                                is LoginEffect.ShowError -> {
                                    snackbarHostState.showSnackbar(
                                        message = event.msg,
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true,
                                    )
                                }
                            }
                        }
                    }

                    LoginScreen(
                        state = state.value,
                        onEvent = vm::onEvent,
                        onNavigateToRegister = { navController.navigate(RegisterRoute) },
                        contentPadding = innerPadding,
                        onGoogleSignIn = { onGoogleSignInClick() }
                    )

                }

                composable<RegisterRoute> {
                    val vm: RegisterViewModel = koinViewModel()
                    val state = vm.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(Unit){
                        vm.effect.collect { event ->
                            when(event){
                                RegisterEffect.NavigateHome -> Unit
                                is RegisterEffect.ShowError -> {
                                    snackbarHostState.showSnackbar(
                                        message = event.msg,
                                        duration = SnackbarDuration.Short,
                                        withDismissAction = true,
                                    )
                                }
                            }
                        }
                    }

                    RegisterScreen(
                        state = state.value,
                        onEvent = vm::onEvent,
                        contentPadding = innerPadding
                    )



                }

            }
        }
    }


}