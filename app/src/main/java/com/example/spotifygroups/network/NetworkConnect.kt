package com.example.spotifygroups.network

import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL


fun <T : Any> getRequest(
    url: String,
    contentType: String,
    accept: String,
    authToken: String,
    returnModel: T
): T {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("Content-Type", contentType)
    connection.setRequestProperty("Authorization", authToken)
    connection.setRequestProperty("Accept", accept)
    connection.doInput = true
    val data = connection.inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(data, returnModel::class.java)
}

fun <T : Any, B : Any> postRequest(url: String, authToken: String, body: B, returnModel: T): T {
    val jsonString: String = Gson().toJson(body)
    val connection = URL(url).openConnection() as HttpURLConnection

    connection.requestMethod = "POST"

    connection.setRequestProperty("Authorization", authToken)
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    connection.doOutput = true
    connection.doInput = true

    connection.outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
    val data = connection.inputStream.bufferedReader().use { it.readText() }
    connection.disconnect()

    return Gson().fromJson(data, returnModel::class.java)
}

fun <T : Any> postRequest(url: String, authToken: String, returnModel: T): T {
    val connection = URL(url).openConnection() as HttpURLConnection

    connection.requestMethod = "POST"

    connection.setRequestProperty("Authorization", authToken)
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    connection.doInput = true
    val data = connection.inputStream.bufferedReader().use { it.readText() }
    connection.disconnect()

    return Gson().fromJson(data, returnModel::class.java)
}

fun <T : Any, B : Any> putRequest(url: String, authToken: String, body: B, returnModel: T): T {
    val jsonString: String = Gson().toJson(body)
    val connection = URL(url).openConnection() as HttpURLConnection

    connection.requestMethod = "PUT"

    connection.setRequestProperty("Authorization", authToken)
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    connection.doOutput = true
    connection.doInput = true

    connection.outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
    val data = connection.inputStream.bufferedReader().use { it.readText() }
    connection.disconnect()

    return Gson().fromJson(data, returnModel::class.java)
}

fun <T : Any> putRequest(url: String, authToken: String, returnModel: T): T {
    val connection = URL(url).openConnection() as HttpURLConnection

    connection.requestMethod = "PUT"

    connection.setRequestProperty("Authorization", authToken)
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    connection.doInput = true
    val data = connection.inputStream.bufferedReader().use { it.readText() }
    connection.disconnect()

    return Gson().fromJson(data, returnModel::class.java)
}

fun <T : Any> deleteRequest(url: String, authToken: String, returnModel: T): T {
    val connection = URL(url).openConnection() as HttpURLConnection

    connection.requestMethod = "DELETE"

    connection.setRequestProperty("Authorization", authToken)
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    connection.doInput = true
    val data = connection.inputStream.bufferedReader().use { it.readText() }
    connection.disconnect()

    return Gson().fromJson(data, returnModel::class.java)
}