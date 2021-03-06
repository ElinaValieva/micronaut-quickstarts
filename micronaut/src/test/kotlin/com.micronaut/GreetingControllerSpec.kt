package com.micronaut

import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

object GreetingControllerSpec : Spek({
    describe("GreetingControllerTest Suite") {
        val embeddedServer: EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java)
        val client: HttpClient = HttpClient.create(embeddedServer.url)

        it("test /hello responds from Micronaut!") {
            val rsp: String = client.toBlocking().retrieve("/hello")
            assertEquals("Hello from Micronaut!", rsp)
        }

        it("test /hello/user responds from Micronaut!") {
            val rsp: String = client.toBlocking().retrieve("/hello/user")
            assertEquals("Hello from Micronaut user!", rsp)
        }

        it("test /greeting responds from Micronaut!") {
            val rsp: String = client.toBlocking().retrieve("/greeting")
            assertEquals("Hello from Micronaut!", rsp)
        }

        it("test /greeting/user responds from Micronaut!") {
            val rsp: String = client.toBlocking().retrieve("/greeting/user")
            assertEquals("Hello from Micronaut user!", rsp)
        }

        afterGroup {
            client.close()
            embeddedServer.close()
        }
    }
})