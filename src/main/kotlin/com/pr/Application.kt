package com.pr

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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

var tables = ArrayList<Table>()
var waiterLock = ReentrantLock()
var servingLock = ReentrantLock()
var rating = Rating()

var finishedOrderList = ConcurrentHashMap<Int,FinishedOrder>()

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
                finishedOrderList.put(finOrd.table_id,finOrd)
                //Answers the server with ok
                call.respondText("Okay", status= HttpStatusCode.Created)

            }
        }
    }.start(wait = false)
    rating.start()
    var waiters = ArrayList<Waiter>()
    for (i in 1..Constants.NR_OF_WAITERS){
        var w = Waiter()
        w.setId(i)
        waiters.add(w)
    }
    for (i in 0..Constants.NR_OF_TABLES - 1){
        tables.add(Table())
        tables[i].setTabId(i)
    }
    for (w in waiters) w.start()
    for(t in tables) t.start()
}
