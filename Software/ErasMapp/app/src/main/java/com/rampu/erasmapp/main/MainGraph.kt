package com.rampu.erasmapp.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rampu.erasmapp.ui.theme.ErasMappTheme

@Composable
fun MainGraph(
    onSignOut: () -> Unit
){
    val navController = rememberNavController()

    ErasMappTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ){
                composable<HomeRoute> {
                    HomeScreen(onSignOut = onSignOut)
                }
            }
        }
    }


}