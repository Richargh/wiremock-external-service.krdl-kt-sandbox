package de.richargh.sandbox.wiremock.externalservice

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpTimeoutException
import java.time.Duration

class HttpAuthService(
        private val authUri: String,
        private val mapper: ObjectMapper,
        private val timeout: Duration = Duration.ofSeconds(10)) {

    private val httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(timeout)
            .build()

    fun validateUser(user: String, password: String): AuthResult {
        val request = HttpRequest.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .GET()
                .uri(URI.create("$authUri?user=$user&password=$password"))
                .timeout(timeout)
                .build()

        val response = try {
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            mapper.readValue(response.body(), AuthResult.OK::class.java)
        } catch (ex: HttpTimeoutException) {
            AuthResult.Timeout
        } catch (ex: JsonMappingException) {
            AuthResult.UnexpectedFormat
        } catch (ex: IOException) {
            AuthResult.MalformedResponse
        }

        return response
    }
}