package com.example.spotifygroups.data

import android.app.Activity
import android.util.Log
import com.example.spotifygroups.ConnectListener
import com.example.spotifygroups.datamodel.GetQueueModel
import com.example.spotifygroups.datamodel.GetTracksModel
import com.example.spotifygroups.datamodel.Image
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.datamodel.QPlayable
import com.example.spotifygroups.datamodel.SavedTracksModel
import com.example.spotifygroups.datamodel.SearchTracksModel
import com.example.spotifygroups.datamodel.SecretsModel
import com.example.spotifygroups.datamodel.SpotifyUserModel
import com.example.spotifygroups.network.getRequest
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SpotifyRepository(private val context: Activity, private val secretsModel: SecretsModel) {
    private var _token: String? = null
    lateinit var loggedInUser: SpotifyUserModel
    private lateinit var _spotifyAppRemote: SpotifyAppRemote

    fun saveToken(token: String) {
        Log.i("SR", token)
        _token = token
    }

    fun checkToken(): Boolean {
        return _token != null
    }

    fun getToken(): String {
        return if (checkToken()) {
            _token!!
        } else {
            ""
        }
    }

    fun connectApp() {
        connect {
            _spotifyAppRemote = it
        }
    }

    private fun connect(callback: (SpotifyAppRemote) -> Unit) {
        val connectParams =
            ConnectionParams.Builder(secretsModel.clientId).setRedirectUri(secretsModel.redirectUri)
                .showAuthView(true)
                .build()
        SpotifyAppRemote.connect(context, connectParams, ConnectListener(callback))
    }

    fun disconnect() {
        if (this::_spotifyAppRemote.isInitialized) {
            _spotifyAppRemote.let {
                SpotifyAppRemote.disconnect(it)
            }
        }
    }

    fun isSpotifyTrackLoaded(): Boolean {
        return try {
            val data = _spotifyAppRemote.playerApi.playerState.await()
            if (!data.isSuccessful) {
                false
            } else {
                val track = data.data.track
                track !== null
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    fun getCurrentPlayingTrack(): Pair<Playable, Long>? {
        try {
            val data = _spotifyAppRemote.playerApi.playerState.await()
            if (!data.isSuccessful) {
                throw Exception("unable to get track")
            }
            val track = data.data.track
            return Pair(
                Playable(
                    artists = track.artists,
                    duration = track.duration,
                    explicit = false,
                    href = "",
                    id = "",
                    isPlayable = true,
                    name = track.name,
                    uri = track.uri
                ), data.data.playbackPosition
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    fun playTrack(playable: QPlayable, withCoroutine: Boolean) {
        if (withCoroutine) {
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        _spotifyAppRemote.playerApi.play(playable.uri)
                    } catch (ex: Exception) {
                        Log.i("SR", "Unable to play track")
                    }
                }
            }
        } else {
            try {
                _spotifyAppRemote.playerApi.play(playable.uri)
            } catch (ex: Exception) {
                Log.i("SR", "Unable to play track")
            }
        }
    }

    fun resume(withCoroutine: Boolean) {
        if (withCoroutine) {
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        _spotifyAppRemote.playerApi.resume()
                    } catch (ex: Exception) {
                        Log.i("SR", "Unable to resume playback")
                    }
                }
            }
        } else {
            try {
                _spotifyAppRemote.playerApi.resume()
            } catch (ex: Exception) {
                Log.i("SR", "Unable to resume playback")
            }
        }
    }

    fun pause(withCoroutine: Boolean) {
        if (withCoroutine) {
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        _spotifyAppRemote.playerApi.pause()
                    } catch (ex: Exception) {
                        Log.i("SR", "Unable to pause playback")
                    }
                }
            }
        } else {
            try {
                _spotifyAppRemote.playerApi.pause()
            } catch (ex: Exception) {
                Log.i("SR", "Unable to pause playback")
            }
        }
    }

    fun addTrackToQueue(uri: String) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    _spotifyAppRemote.playerApi.queue(uri).await()
                } catch (ex: Exception) {
                    Log.e("SR", "Unable to add track to queue")
                }
            }
        }
    }

    fun searchTracks(
        query: String,
        type: String = "track",
        market: String = "ES",
        limit: Int = 20
    ): List<QPlayable> {
        try {
            var url = "https://api.spotify.com/v1/search"
            url = url.plus("?q=$query")
            url = url.plus("&type=$type")
            url = url.plus("&market=$market")
            url = url.plus("&limit=$limit")
            url = url.replace(" ", "+")
            val contentTypeAccept = "application/json"
            val authToken = "Bearer $_token"
            val data = getRequest(
                url,
                contentTypeAccept,
                contentTypeAccept,
                authToken,
                SearchTracksModel()
            )
            return data.tracks.items.map { playable ->
                QPlayable(
                    image = getImage(playable),
                    artists = playable.artists.map { it.name },
                    duration = playable.duration,
                    name = playable.name,
                    spotifyId = playable.id,
                    uri = playable.uri
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return emptyList()
    }

    private fun getImage(playable: Playable): Image? {
        var image = playable.album!!.images.find { it.width < 100 }
        if (image == null) {
            image = playable.album.images[playable.album.images.lastIndex]
        }
        return image
    }

    fun getTracks(trackIds: List<String>): List<Playable> {
        try {
            val queryParam = trackIds.joinToString(",")
            var url = "https://api.spotify.com/v1/tracks"
            url = url.plus("?ids=$queryParam")
            val contentTypeAccept = "application/json"
            val authToken = "Bearer $_token"
            val data =
                getRequest(url, contentTypeAccept, contentTypeAccept, authToken, GetTracksModel())
            return data.tracks
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return emptyList()
    }

    fun getSpotifyUserProfile() {
        try {
            val url = "https://api.spotify.com/v1/me"
            val contentTypeAccept = "application/json"
            val authToken = "Bearer $_token"
            loggedInUser = getRequest(
                url,
                contentTypeAccept,
                contentTypeAccept,
                authToken,
                SpotifyUserModel("", "", "")
            )
        } catch (err: Exception) {
            err.printStackTrace()
        }
    }
}