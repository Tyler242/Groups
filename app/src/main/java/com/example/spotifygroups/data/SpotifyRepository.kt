package com.example.spotifygroups.data

import android.app.Activity
import android.util.Log
import com.example.spotifygroups.ConnectListener
import com.example.spotifygroups.datamodel.GetQueueModel
import com.example.spotifygroups.datamodel.GetTracksModel
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.datamodel.SavedTracksModel
import com.example.spotifygroups.datamodel.SearchTracksModel
import com.example.spotifygroups.datamodel.SpotifyUserModel
import com.example.spotifygroups.network.getRequest
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.ContentApi
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.ListItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SpotifyRepository(private val context: Activity) {
    private val CLIENT_ID = "9adc315ded0746659f31baa5226bdcbb"
    private val REDIRECT_URI = "spotifygroups://callback"
    private var _token: String? = null
    lateinit var loggedInUser: SpotifyUserModel
    private lateinit var _spotifyAppRemote: SpotifyAppRemote

    fun saveToken(token: String) {
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
            ConnectionParams.Builder(CLIENT_ID).setRedirectUri(REDIRECT_URI).showAuthView(true)
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

    fun getSpotifyAppRemote(): SpotifyAppRemote {
        return _spotifyAppRemote
    }

    fun getPlayState(callback: (Boolean) -> Unit) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val isPaused = (_spotifyAppRemote.playerApi.playerState.await()).data.isPaused
                callback(isPaused)
            }
        }
    }

    fun updatePlayState(callback: (Boolean) -> Unit) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val isPaused = (_spotifyAppRemote.playerApi.playerState.await()).data.isPaused
                if (isPaused) _spotifyAppRemote.playerApi.resume()
                else _spotifyAppRemote.playerApi.pause()
                callback(!isPaused)
            }
        }
    }

    fun getRecommendedContentItems(type: String = ContentApi.ContentType.DEFAULT, callback: (ListItems) -> Unit) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val result = _spotifyAppRemote.contentApi.getRecommendedContentItems(type).await()
                if (result.isSuccessful) callback(result.data)
            }
        }
    }

    fun searchTracks(query: String, type: String = "track", market: String = "ES", limit: Int = 20): List<Playable> {
        try {
            var url = "https://api.spotify.com/v1/search"
            url = url.plus("?q=$query")
            url = url.plus("&type=$type")
            url = url.plus("&market=$market")
            url = url.plus("&limit=$limit")
            url = url.replace(" ", "+")
            val contentTypeAccept = "application/json"
            val authToken = "Bearer $_token"
            val data = getRequest(url, contentTypeAccept, contentTypeAccept, authToken, SearchTracksModel())
            return data.tracks.items
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return emptyList()
    }

    fun getTracks(trackIds: List<String>): List<Playable> {
        try {
            val queryParam = trackIds.joinToString(",")
            var url = "https://api.spotify.com/v1/tracks"
            url = url.plus("?ids=$queryParam")
            val contentTypeAccept = "application/json"
            val authToken = "Bearer $_token"
            val data = getRequest(url, contentTypeAccept, contentTypeAccept, authToken, GetTracksModel())
            return data.tracks
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return emptyList()
    }

    fun getGenreSeeds(): List<String> {
        try {
            val url = "https://api.spotify.com/v1/recommendations/available-genre-seeds"
            val contentTypeAccept = "application/json"
            val authToken = "Bearer $_token"
            return getRequest(url, contentTypeAccept, contentTypeAccept, authToken, listOf())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return emptyList()
    }

    fun getUsersSavedTracks(): List<Playable> {
        try {
            val url = "https://api.spotify.com/v1/me/tracks"
            val contentTypeAccept = "application/json"
            val authToken = "Bearer $_token"
            val data = getRequest(url, contentTypeAccept, contentTypeAccept, authToken, SavedTracksModel())
            return data.items.map {
                it.track
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return emptyList()
    }

    fun getQueue(): List<Playable> {
        try {
            val url = "https://api.spotify.com/v1/me/player/queue"
            val contentTypeAccept = "application/json"
            val authToken = "Bearer $_token"
            val data = getRequest(url, contentTypeAccept, contentTypeAccept, authToken, GetQueueModel())
            return data.queue.filter { it.type == "track" }
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
            loggedInUser = getRequest(url, contentTypeAccept, contentTypeAccept, authToken, SpotifyUserModel("", "", ""))
        } catch (err: Exception) {
            err.printStackTrace()
        }
    }
}