package com.example.spotifygroups

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.spotifygroups.data.SpotifyRepository
import com.example.spotifygroups.data.UserRepository
import com.example.spotifygroups.datamodel.SecretsModel
import com.example.spotifygroups.network.getRequest
import com.example.spotifygroups.ui.theme.DitestTheme
import com.example.spotifygroups.uistatemodel.View
import com.example.spotifygroups.view.HomeView
import com.example.spotifygroups.view.SessionView
import com.example.spotifygroups.viewmodel.AppViewModel
import com.example.spotifygroups.viewmodel.SpotifyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private val userRepository = UserRepository()
    private lateinit var spotifyRepository: SpotifyRepository
    private lateinit var spotifyViewModel: SpotifyViewModel
    private lateinit var appViewModel: AppViewModel
    private lateinit var observer: MainLifecycleObserver
    private lateinit var secretsModel: SecretsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                secretsModel = getSecrets()
            }
            appViewModel = AppViewModel()

            job.join()

            spotifyRepository = SpotifyRepository(this@MainActivity, secretsModel)
            spotifyViewModel = SpotifyViewModel(spotifyRepository, userRepository)
            observer = MainLifecycleObserver(activityResultRegistry, spotifyViewModel, secretsModel)
            lifecycle.addObserver(observer)

//            val sbr: BroadcastReceiver = SpotifyBroadcastReceiver()
//            val filter = IntentFilter("com.spotify.music.queuechanged")
//            ContextCompat.registerReceiver(this@MainActivity, sbr, filter, ContextCompat.RECEIVER_EXPORTED)
        }
    }

    override fun onStart() {
        super.onStart()
        observer.spotifyLogin(this)
        setContent {
            DitestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val spotifyUiState by spotifyViewModel.uiState.collectAsState()
                    val appState by appViewModel.uiState.collectAsState()
                    if (appState.view == View.HOME) HomeView(appViewModel)
                    else if (appState.view == View.SESSION) SessionView(
                        spotifyRepository,
                        userRepository
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyViewModel.disconnect()
    }

    private fun getSecrets(): SecretsModel {
        val url = "https://spotify-groups-api.onrender.com/secrets"
        return getRequest(url, "application/json", "application/json", "Bearer fjls@!*4jal*FJle_428", SecretsModel("", ""))
    }
}