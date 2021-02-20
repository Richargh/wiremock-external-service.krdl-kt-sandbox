package de.richargh.sandbox.spock.wiremock.externalservice

data class AuthResult(
        val user: String,
        val isAuthenticated: Boolean)