package de.richargh.sandbox.jpms

object Deps {

    /** Main dependencies **/
    object Jackson { const val version = "2.12.1" }
    object Slf4j { const val version = "1.7.30" }

    /** Test dependencies **/
    object Junit { const val version = "5.7.1" }
    object AssertJ { const val version = "3.19.0" }
    object Wiremock { const val version = "2.27.2" }
}