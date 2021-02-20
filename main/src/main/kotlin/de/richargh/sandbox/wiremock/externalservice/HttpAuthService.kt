package de.richargh.sandbox.wiremock.externalservice

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class HttpAuthService(
        private val authUri: String,
        private val mapper: ObjectMapper){

    private val httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build()

    fun validateUser(user: String, password: String): AuthResult {
        val request = HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .GET()
                .uri(URI.create("$authUri?user=$user&password=$password"))
                .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return mapper.readValue(response.body(), AuthResult::class.java)
    }


}