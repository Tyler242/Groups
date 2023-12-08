package com.example.spotifygroups.data

import com.example.spotifygroups.datamodel.AuthRequestModel
import com.example.spotifygroups.datamodel.AuthResultModel
import com.example.spotifygroups.datamodel.AuthTokenModel
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.datamodel.QueueAddRequestModel
import com.example.spotifygroups.datamodel.QueueResultModel
import com.example.spotifygroups.datamodel.SpotifyUserModel
import com.example.spotifygroups.network.deleteRequest
import com.example.spotifygroups.network.postRequest
import com.example.spotifygroups.network.putRequest

class UserRepository {
    private lateinit var token: String
    private lateinit var userId: String
    private lateinit var queueId: String

    fun authenticate(user: SpotifyUserModel) {
        try {
            val url = "https://spotify-groups-api.onrender.com/auth/login"
            val data: AuthResultModel = postRequest(
                url, "", AuthRequestModel(user.email, user.spotifyUuid), AuthResultModel(
                    AuthTokenModel("", 0),
                    "",
                    "",
                    ""
                )
            )
            token = data.authTokenModel.token
            userId = data.userId
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun createQueue(): QueueResultModel {
        return try {
            val url = "https://spotify-groups-api.onrender.com/queue"
            val data: QueueResultModel = postRequest(
                url,
                "Bearer $token",
                QueueResultModel(_id = "", creatorId = "")
            )
            queueId = data._id
            data
        } catch (ex: Exception) {
            ex.printStackTrace()
            QueueResultModel(_id = "", creatorId = "")
        }
    }

    fun addToQueue(trackId: String): QueueResultModel {
        val resultModel = QueueResultModel(_id = "", creatorId = "")
        try {
            val url = "https://spotify-groups-api.onrender.com/queue/$queueId"
            return postRequest(
                url,
                "Bearer $token",
                QueueAddRequestModel(trackId),
                resultModel
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun removeFromQueue(trackId: String): QueueResultModel {
        val resultModel = QueueResultModel(_id = "", creatorId = "")
        try {
            val url = "https://spotify-groups-api.onrender.com/queue/$queueId/$trackId"
            return deleteRequest(url, "Bearer $token", resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun updateQueue(trackId: String, index: Int): QueueResultModel {
        val resultModel = QueueResultModel(_id = "", creatorId = "")
        try {
            val url = "https://spotify-groups-api.onrender.com/queue/$queueId/$trackId/$index"
            return putRequest(url, "Bearer $token", resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun incrementQueue(): QueueResultModel {
        val resultModel = QueueResultModel(_id = "", creatorId = "")
        try {
            val url = "https://spotify-groups-api.onrender.com/queue/$queueId"
            return putRequest(url, "Bearer $token", resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun playPauseQueue(pause: Boolean): QueueResultModel {
        val resultModel = QueueResultModel(_id = "", creatorId = "")
        try {
            val url = "https://spotify-groups-api.onrender.com/queue/$queueId/${if (pause) "pause" else "play"}"
            return putRequest(url, "Bearer $token", resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun getToken(): String {
        return token
    }

    fun getUserId(): String {
        return userId
    }
}