package com.madetolive.server.config

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory

object GoogleTokenVerifier {

    private val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
        .setAudience(listOf("YOUR_GOOGLE_CLIENT_ID")) // Replace with your OAuth Client ID
        .build()

    fun verify(idToken: String): VerifiedUser? {
        val googleIdToken = verifier.verify(idToken) ?: return null
        val payload = googleIdToken.payload

        return VerifiedUser(
            googleId = payload.subject,
            email = payload.email,
            name = payload["name"] as String? ?: "Unknown User"
        )
    }
}

data class VerifiedUser(
    val googleId: String,
    val email: String,
    val name: String
)