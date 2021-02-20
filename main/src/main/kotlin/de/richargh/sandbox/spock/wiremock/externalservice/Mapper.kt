package de.richargh.sandbox.spock.wiremock.externalservice

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun mapper() = jacksonObjectMapper().apply {
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val module = SimpleModule()
    registerModule(module)
    registerModule(KotlinModule())
}