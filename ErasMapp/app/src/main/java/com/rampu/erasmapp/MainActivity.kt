package com.rampu.erasmapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rampu.erasmapp.auth.ui.AuthGraph
import com.rampu.erasmapp.common.ui.LoadingScreen
import com.rampu.erasmapp.main.MainGraph
import com.rampu.erasmapp.session.SessionViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppRoot() }
    }
}

@Composable
fun AppRoot(){
    val sessionViewModel: SessionViewModel = koinViewModel()
    val sessionState by sessionViewModel.state.collectAsStateWithLifecycle()

    if(sessionState.isLoadingUserStatus){
        LoadingScreen()
        return
    }

    if(sessionState.user != null)
        MainGraph(
            onSignOut = {sessionViewModel.signOut()}
        )

    else{
        AuthGraph()
    }

}