package com.madetolive.server

import com.madetolive.server.config.SupabaseConfig
import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

@Service
class SupabaseStorageService(
    private val config: SupabaseConfig
) {

    private val client = HttpClient.newBuilder().build()

    fun uploadImage(userId: Long, fileName: String, contentType: String, data: ByteArray): String {
        val path = "users/$userId/$fileName"
        val uri = URI.create("${config.url}/storage/v1/object/profile-images/$path")

        val request = HttpRequest.newBuilder()
            .uri(uri)
            .header("apikey", config.serviceKey)
            .header("Authorization", "Bearer ${config.serviceKey}")
            .header("Content-Type", contentType)
            .PUT(HttpRequest.BodyPublishers.ofByteArray(data))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw RuntimeException("Failed to upload image to Supabase: ${response.body()}")
        }

        return "${config.url}/storage/v1/object/public/profile-images/$path"
    }

    fun deleteAllUserImages(userId: Long) {
        val path = "users/$userId"

        // List all objects under the user folder
        val listUri = URI.create("${config.url}/storage/v1/object/list/profile-images")
        val requestBody = """
        {
            "prefix": "$path/"
        }
    """.trimIndent()

        val listRequest = HttpRequest.newBuilder()
            .uri(listUri)
            .header("apikey", config.serviceKey)
            .header("Authorization", "Bearer ${config.serviceKey}")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        val listResponse = client.send(listRequest, HttpResponse.BodyHandlers.ofString())
        if (listResponse.statusCode() != 200) {
            throw RuntimeException("Failed to list images: ${listResponse.body()}")
        }

        val jsonArray = JSONArray(listResponse.body())
        val objectPaths = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            objectPaths.add(obj.getString("name"))
        }

        if (objectPaths.isEmpty()) return

        // Delete the objects
        val deleteUri = URI.create("${config.url}/storage/v1/object/profile-images")
        val deleteJson = JSONObject()
        deleteJson.put("prefixes", JSONArray(objectPaths))
        val deleteBody = deleteJson.toString()

        val deleteRequest = HttpRequest.newBuilder()
            .uri(deleteUri)
            .header("apikey", config.serviceKey)
            .header("Authorization", "Bearer ${config.serviceKey}")
            .header("Content-Type", "application/json")
            .method("DELETE", HttpRequest.BodyPublishers.ofString(deleteBody))
            .build()

        val deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString())
        if (deleteResponse.statusCode() != 200) {
            throw RuntimeException("Failed to delete old images: ${deleteResponse.body()}")
        }
    }
}