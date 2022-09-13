package pr

import com.pr.OrderManager
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import pr.plugins.*
import java.util.concurrent.locks.ReentrantLock

var manager = OrderManager()
var tableMapping = HashMap<Int, Int>()
var waiterLock = ReentrantLock()
var servingLock = ReentrantLock()
var finishedOrderList = ArrayList<FinishedOrder>()

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureRouting()
    }.start(wait = false)
    var waiters = ArrayList<Waiter>()
    for (i in 1..Constants.NR_OF_WAITERS){
        var w = Waiter()
        w.setId(i)
        waiters.add(w)
    }
    manager.start()
    for (w in waiters) w.start()
}
