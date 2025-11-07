package com.rampu.erasmapp.auth.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rampu.erasmapp.auth.ui.login.LoginEffect
import com.rampu.erasmapp.auth.ui.login.LoginScreen
import com.rampu.erasmapp.auth.ui.login.LoginViewModel
import com.rampu.erasmapp.auth.ui.register.RegisterEffect
import com.rampu.erasmapp.auth.ui.register.RegisterScreen
import com.rampu.erasmapp.auth.ui.register.RegisterViewModel
import com.rampu.erasmapp.ui.theme.ErasMappTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthGraph(){
    val navController = rememberNavController()

    ErasMappTheme {
        Scaffold(
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

                    LaunchedEffect(Unit) {
                        vm.effect.collect { event ->
                            when(event){
                                LoginEffect.NavigateHome -> Unit
                                is LoginEffect.ShowError -> Unit //TODO: need to implement some action when login fails
                            }
                        }
                    }

                    LoginScreen(
                        state = state.value,
                        onEvent = vm::onEvent,
                        onNavigateToRegister = {navController.navigate(RegisterRoute)},
                        contentPadding = innerPadding
                    )

                }

                composable<RegisterRoute> {
                    val vm: RegisterViewModel = koinViewModel()
                    val state = vm.uiState.collectAsStateWithLifecycle()

                    LaunchedEffect(Unit){
                        vm.effect.collect { event ->
                            when(event){
                                RegisterEffect.NavigateHome -> Unit
                                is RegisterEffect.ShowError -> Unit //TODO: need to implement some action when registration fails
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