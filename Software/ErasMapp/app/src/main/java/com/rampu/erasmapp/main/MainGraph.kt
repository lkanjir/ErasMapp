package com.rampu.erasmapp.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainGraph(
    onSignOut: () -> Unit
){
    val navController = rememberNavController()

    //TODO: wrap in Scaffold so we get the same top / bottom bar on all screens
    NavHost(navController, startDestination = HomeRoute){
        composable<HomeRoute> {
            HomeScreen(onSignOut = onSignOut)
        }
    }

}