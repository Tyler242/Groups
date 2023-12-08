//package com.example.spotifygroups
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//
//class SpotifyBroadcastReceiver: BroadcastReceiver() {
//    internal object BroadcastTypes {
//        const val SPOTIFY_PACKAGE = "com.spotify.music"
//        const val PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged"
//        const val QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged"
//        const val METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged"
//    }
//    override fun onReceive(context: Context, intent: Intent) {
//
//        when (intent.action!!) {
//            BroadcastTypes.METADATA_CHANGED -> Log.i("SBR", intent.toString())
//            BroadcastTypes.PLAYBACK_STATE_CHANGED -> Log.i("SBR", intent.toString())
//            BroadcastTypes.QUEUE_CHANGED -> Log.i("SBR", intent.toString())
//        }
//    }
//}