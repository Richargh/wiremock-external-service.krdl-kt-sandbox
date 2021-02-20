package de.richargh.sandbox.wiremock.externalservice

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.http.Fault
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import java.time.Duration

class HttpAuthServiceTest {

    @BeforeEach
    fun beforeEach() {
        wireMockServer.resetAll()
    }

    @Test
    fun `should return authenticated user, when the server says the user is authenticated`() {
        // arrange
        val user = "John"
        stubFor(get(urlPathMatching("/authentication"))
                        .withHeader("Accept", equalTo("application/json"))
                        .willReturn(aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", "application/json")
                                            .withBody("""
                                                |{ 
                                                |   "user": "$user",
                                                |   "isAuthenticated": true 
                                                |}
                                                |""".trimMargin())))

        val testling = HttpAuthService("http://localhost:$port/authentication", mapper())

        // act
        val result = testling.validateUser(user, "SkynetSucks")

        // assert
        assertThat(result).isEqualTo(AuthResult.OK(user, isAuthenticated = true))
    }

    @Test
    fun `should return non-authenticated user, when the server says the user is not authenticated`() {
        // arrange
        val user = "John"
        stubFor(get(urlPathMatching("/authentication"))
                        .withHeader("Accept", equalTo("application/json"))
                        .willReturn(aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", "application/json")
                                            .withBody("""
                                                |{ 
                                                |   "user": "$user",
                                                |   "isAuthenticated": false 
                                                |}
                                                |""".trimMargin())))

        val testling = HttpAuthService("http://localhost:$port/authentication", mapper())

        // act
        val result = testling.validateUser(user, "SkynetSucks")

        // assert
        assertThat(result).isEqualTo(AuthResult.OK(user, isAuthenticated = false))
    }

    @Test
    fun `should return an timeout, when the server delays`() {
        // arrange
        val user = "John"
        stubFor(get(urlPathMatching("/authentication"))
                        .withHeader("Accept", equalTo("application/json"))
                        .willReturn(aResponse()
                                            .withStatus(200)
                                            .withFixedDelay(2000)))

        val testling = HttpAuthService(
                "http://localhost:$port/authentication",
                mapper(),
                timeout = Duration.ofMillis(1000))

        // act
        val result = testling.validateUser(user, "SkynetSucks")

        // assert
        assertThat(result).isEqualTo(AuthResult.Timeout)
    }

    @Test
    fun `should notify when the server sends a malformed response`() {
        // arrange
        val user = "John"
        stubFor(get(urlPathMatching("/authentication"))
                        .withHeader("Accept", equalTo("application/json"))
                        .willReturn(aResponse()
                                            .withStatus(200)
                                            .withFault(Fault.MALFORMED_RESPONSE_CHUNK)))

        val testling = HttpAuthService(
                "http://localhost:$port/authentication",
                mapper())

        // act
        val result = testling.validateUser(user, "SkynetSucks")

        // assert
        assertThat(result).isEqualTo(AuthResult.MalformedResponse)
    }

    @Test
    fun `should notify when the server sends a response in an unexpected format`() {
        // arrange
        val user = "John"
        stubFor(get(urlPathMatching("/authentication"))
                        .withHeader("Accept", equalTo("application/json"))
                        .willReturn(aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", "application/json")
                                            .withBody("""
                                                |{ 
                                                |   "grfzl": "supp",
                                                |   "isAuthenticated": false 
                                                |}
                                                |""".trimMargin())))

        val testling = HttpAuthService(
                "http://localhost:$port/authentication",
                mapper())

        // act
        val result = testling.validateUser(user, "SkynetSucks")

        // assert
        assertThat(result).isEqualTo(AuthResult.UnexpectedFormat)
    }

    companion object {

        private lateinit var wireMockServer: WireMockServer

        private val port = 8089

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            wireMockServer = WireMockServer(wireMockConfig().port(port))
            configureFor("localhost", port)
            wireMockServer.start()
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            wireMockServer.stop()
        }
    }
}