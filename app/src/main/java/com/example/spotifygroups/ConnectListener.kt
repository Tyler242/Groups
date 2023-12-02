package com.example.spotifygroups

import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

class ConnectListener(val onConnectCallback: (SpotifyAppRemote) -> Unit) :
    Connector.ConnectionListener {
    override fun onConnected(spotifyAppRemote: SpotifyAppRemote?) {
        try {
            onConnectCallback(spotifyAppRemote!!)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onFailure(error: Throwable?) {
        if (error != null) {
            println(error.message)
        }
    }
}