package de.richargh.sandbox.wiremock.externalservice

sealed class AuthResult: SealedEnum() {

    data class OK(
            val user: String,
            val isAuthenticated: Boolean): AuthResult()

    object Timeout: AuthResult()

    object MalformedResponse: AuthResult()

    object UnexpectedFormat: AuthResult()
}