package com.example.spotifygroups.data

import com.example.spotifygroups.datamodel.FriendResultModel
import com.example.spotifygroups.datamodel.UserSearchResultModel
import com.example.spotifygroups.network.deleteRequest
import com.example.spotifygroups.network.getRequest
import com.example.spotifygroups.network.postRequest

class FriendRepository(private val userId: String, private val token: String) {
    fun getFriends(): FriendResultModel {
        val resultModel = FriendResultModel(userId = "")
        try {
            val url = "https://spotify-groups-api.onrender.com/friends"
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

    fun searchUsers(query: String): UserSearchResultModel {
        val resultModel = UserSearchResultModel(0)
        try {
            val url = "https://spotify-groups-api.onrender.com/friends/search"
            val body = object {
                val query = query
            }
            return postRequest(url, "Bearer $token", body, resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun addFriend(friendUserId: String): FriendResultModel {
        val resultModel = FriendResultModel(userId = "")
        try {
            val url = "https://spotify-groups-api.onrender.com/friends"
            val body = object {
                val friendUserId = friendUserId
            }
            return postRequest(url, "Bearer $token", body, resultModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return resultModel
    }

    fun removeFriend(friendUserId: String): FriendResultModel {
        val returnModel = FriendResultModel(userId = "")
        try {
            val url = "https://spotify-groups-api.onrender.com/friends/$friendUserId"
            return deleteRequest(url, "Bearer $token", returnModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return returnModel
    }

    fun getUserId(): String {
        return userId
    }
}