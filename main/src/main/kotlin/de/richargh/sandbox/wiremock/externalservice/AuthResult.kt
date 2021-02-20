package de.richargh.sandbox.wiremock.externalservice

data class AuthResult(
        val user: String,
        val isAuthenticated: Boolean)