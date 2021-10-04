import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.html.*
import java.net.URL

fun main(): Unit {
    embeddedServer(CIO, System.getenv("WEB_PORT").toInt()) {
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        meta(charset = "utf-8")
                        meta(name = "viewport", content = "width=device-width, initial-scale=1, shrink-to-fit=no")
                        title("Warscape")
                    }
                    body {
                        div {
                            id = "root"
                        }
                        noScript {
                            +"Please enable javascript to run this page"
                        }
                        script (
                            src = "/static/main.bundle.js"
                        ) {}
                    }
                }
            }
            static("/static") {
                resources()
            }
        }
    }.start(wait = true)
}