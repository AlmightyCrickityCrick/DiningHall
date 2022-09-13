package pr

import com.pr.OrderManager
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import pr.plugins.*
var manager = OrderManager()

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureRouting()
    }.start(wait = false)
    manager.start()

}
