package de.richargh.sandbox.wiremock.externalservice

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class HttpAuthServiceTest {

    @Test
    fun `should return authenticated user, when the server says the user is authenticated`(){
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
        assertThat(result).isEqualTo(AuthResult(user, isAuthenticated = true))
    }

    companion object {

        private lateinit var wireMockServer: WireMockServer

        private val port = 8089

        @BeforeAll
        @JvmStatic
        fun beforeAll(){
            wireMockServer = WireMockServer(wireMockConfig().port(port))
            configureFor("localhost", port)
            wireMockServer.start()
        }

        @AfterAll
        @JvmStatic
        fun afterAll(){
            wireMockServer.stop()
        }
    }
}