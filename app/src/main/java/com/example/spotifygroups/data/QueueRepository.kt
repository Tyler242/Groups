package com.example.spotifygroups.data

import android.util.Log
import com.example.spotifygroups.datamodel.AuthRequestModel
import com.example.spotifygroups.datamodel.AuthResultModel
import com.example.spotifygroups.datamodel.AuthTokenModel
import com.example.spotifygroups.datamodel.FriendQueuesResultModel
import com.example.spotifygroups.datamodel.NameResult
import com.example.spotifygroups.datamodel.QPlayable
import com.example.spotifygroups.datamodel.QueueError
import com.example.spotifygroups.datamodel.QueueResultModel
import com.example.spotifygroups.datamodel.SpotifyUserModel
import com.example.spotifygroups.datamodel.SuccessResponse
import com.example.spotifygroups.network.deleteRequest
import com.example.spotifygroups.network.getRequest
import com.example.spotifygroups.network.postRequest
import com.example.spotifygroups.network.putRequest
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.HttpURLConnection
import java.net.URL

class QueueRepository {
    private lateinit var token: String
    private lateinit var userId: String
    private var queueId: String? = null
    private val baseUrl: String = "https://spotify-groups-api.onrender.com"

    fun authenticate(user: SpotifyUserModel) {
        try {
            val url = "$baseUrl/auth/login"
            val data: AuthResultModel = postRequest(
                url, "", AuthRequestModel(user.email, user.spotifyUuid, user.name), AuthResultModel(
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

    fun setQueueId(queueId: String, joinQueue: Boolean) {
        this.queueId = queueId
        if (joinQueue) {
            runBlocking {
                CoroutineScope(Dispatchers.IO).launch {
                    joinQueue()
                }
            }
        }
    }

    fun isQueueIdSet(): Boolean {
        return queueId !== null
    }

    fun getQueue(): Pair<QueueResultModel, QueueError> {
        val resultModel = QueueResultModel(id = "", creatorId = "")
        try {
            val url = "$baseUrl/queue/$queueId"
            val contentAccept = "application/json"

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Bearer $token")
            connection.setRequestProperty("Accept", "application/json")
            connection.doInput = true

            return if (connection.responseCode == 204) {
                Pair(resultModel, QueueError(false))
            } else {
                val data = connection.inputStream.bufferedReader().use { it.readText() }
                val newResultModel = Gson().fromJson(data, resultModel::class.java)
                Pair(newResultModel, QueueError(true))
            }
        } catch (ex: Exception) {
            Log.e("QR", "${ex.message}")
            ex.printStackTrace()
        }
        return Pair(resultModel, QueueError(false))
    }

    fun createQueue(): QueueResultModel {
        return try {
            val url = "$baseUrl/queue"
            val data: QueueResultModel = postRequest(
                url,
                "Bearer $token",
                QueueResultModel(id = "", creatorId = "")
            )
            queueId = data.id
            data
        } catch (ex: Exception) {
            ex.printStackTrace()
            QueueResultModel(id = "", creatorId = "")
        }
    }

    private fun joinQueue(): QueueResultModel {
        val resultModel = QueueResultModel(id = "", creatorId = "")
        try {
            val url = "$baseUrl/queue/$queueId/user"
            return postRequest(url, "Bearer $token", resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun leaveQueue(): Boolean {
        val resultModel = SuccessResponse()
        try {
            val url = "$baseUrl/queue/$queueId/user"
            return deleteRequest(url, "Bearer $token", resultModel).success
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel.success
    }

    fun removeParticipant(friendId: String): Boolean {
        val resultModel = SuccessResponse()
        try {
            val url = "$baseUrl/queue/$queueId/user/$friendId"
            return deleteRequest(url, "Bearer $token", resultModel).success
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel.success
    }

    fun deleteQueue(): Boolean {
        val resultModel = SuccessResponse()
        try {
            val url = "$baseUrl/queue/$queueId"
            return deleteRequest(url, "Bearer $token", resultModel).success
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel.success
    }

    fun addToQueue(playable: QPlayable): QueueResultModel {
        val resultModel = QueueResultModel(id = "", creatorId = "")
        try {
            val url = "$baseUrl/queue/$queueId"
            return postRequest(
                url,
                "Bearer $token",
                playable,
                resultModel
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun removeFromQueue(trackId: String): QueueResultModel {
        val resultModel = QueueResultModel(id = "", creatorId = "")
        try {
            val url = "$baseUrl/queue/$queueId/$trackId"
            return deleteRequest(url, "Bearer $token", resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun updateQueue(trackId: String, index: Int): QueueResultModel {
        val resultModel = QueueResultModel(id = "", creatorId = "")
        try {
            val url = "$baseUrl/queue/$queueId/$trackId/$index"
            return putRequest(url, "Bearer $token", resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun incrementQueue(): QueueResultModel {
        val resultModel = QueueResultModel(id = "", creatorId = "")
        try {
            val url = "$baseUrl/queue/$queueId"
            return putRequest(url, "Bearer $token", resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun playPauseQueue(pause: Boolean): QueueResultModel {
        val resultModel = QueueResultModel(id = "", creatorId = "")
        try {
            val url = "$baseUrl/queue/$queueId/${if (pause) "pause" else "play"}"
            return putRequest(url, "Bearer $token", resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun getFriendQueues(): FriendQueuesResultModel {
        val resultModel = FriendQueuesResultModel()
        try {
            val url = "$baseUrl/queue/friends"
            return getRequest(
                url,
                "application/json",
                "application/json",
                "Bearer $token",
                resultModel
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun reset() {
        queueId = null
    }

    fun getToken(): String {
        return token
    }

    fun getUserId(): String {
        return userId
    }
}