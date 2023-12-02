package com.example.spotifygroups

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.spotifygroups.datamodel.SecretsModel
import com.example.spotifygroups.viewmodel.SpotifyViewModel
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainLifecycleObserver(
    private val registry: ActivityResultRegistry,
    private val spotifyViewModel: SpotifyViewModel,
    private val secretsModel: SecretsModel
) :
    DefaultLifecycleObserver {

    lateinit var getIntent: ActivityResultLauncher<Intent>
    private lateinit var _token: String




    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        getIntent =
            registry.register("key", owner, ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val intent = it.data!!
                    val bundle: Bundle = intent.getBundleExtra("EXTRA_AUTH_RESPONSE")!!
                    if (bundle.containsKey("response")) {
                        val data: AuthorizationResponse? =
                            (bundle.get("response") as AuthorizationResponse?)
                        _token = data!!.accessToken
                        spotifyViewModel.saveToken(_token)
                    }
                }
            }
    }

    fun spotifyLogin(context: Activity) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val builder =
                    AuthorizationRequest.Builder(
                        secretsModel.clientId,
                        AuthorizationResponse.Type.TOKEN,
                        secretsModel.redirectUri
                    )
                        .setScopes(
                            arrayOf(
                                "user-read-private",
                                "user-read-email",
                                "user-read-recently-played",
                                "user-top-read",
                                "user-read-currently-playing",
                                "playlist-read-collaborative",
                                "playlist-read-private",
                                "user-read-playback-state",
                                "user-library-read"
                            )
                        )
                val request = builder.build()
                getIntent.launch(
                    Intent(
                        AuthorizationClient.createLoginActivityIntent(
                            context,
                            request
                        )
                    )
                )
            }
        }
    }

    fun getToken(): String {
        return _token
    }
}