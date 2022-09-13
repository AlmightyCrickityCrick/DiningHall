package com.pr

import com.pr.OrderManager
import com.pr.plugins.configureSerialization
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import com.pr.plugins.*
import java.util.concurrent.locks.ReentrantLock

var manager = OrderManager()
var tableMapping = HashMap<Int, Int>()
var waiterLock = ReentrantLock()
var servingLock = ReentrantLock()
var finishedOrderList = ArrayList<FinishedOrder>()

fun main() {
    embeddedServer(Netty, port = 8080) {
        configureSerialization()
        routing {
            post("/distribution") {
                //Receive the post request from Kitchen
                val rawOrd = call.receive<String>()
                //Deserialize Json into Finished Order object
                val finOrd = Json.decodeFromString(FinishedOrder.serializer(), rawOrd)
                println("Order ${finOrd.order_id} for ${finOrd.table_id} via ${finOrd.waiter_id} has arrived")
                //Add finished order to the list of all finished orders
                finishedOrderList.add(finOrd)
                //Answers the server with ok
                call.respondText("Okay", status= HttpStatusCode.Created)

            }
        }
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
