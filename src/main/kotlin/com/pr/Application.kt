package com.pr

import Constants
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
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

var tables = ArrayList<Table>()
var waiterLock = ReentrantLock()
var servingLock = ReentrantLock()
var rating = Rating()
var orderTaken = Semaphore(4)

var finishedOrderList = ConcurrentHashMap<Int,FinishedOrder>()
lateinit var rest :Self
var client = HttpClient()
var foodsInWaitingList = AtomicInteger(0)
var foodDeliveryFinishedOrder = ConcurrentHashMap<Int,FinishedOrder>()


fun main() {
    var conf = File("config/config.json").inputStream().readBytes().toString(Charsets.UTF_8)
    rest = Json{coerceInputValues = true}.decodeFromString(Self.serializer(), conf)
    println(rest)
    rating.start()
    embeddedServer(Netty, port = rest.dining_port) {
        configureSerialization()
        configureRouting()
        launch { var resp: HttpResponse = client.post("http://food-ordering:8088"+"/register"){
            setBody(Json.encodeToString(Restaurant.serializer(), Restaurant(rest.restaurant_id, rest.restaurant_name, rest.dining_url, 13, Constants.getMenu(), rating.currentRating)))}
            println("Sent Registration request")
        }

        routing {
            post("/distribution") {
                //Receive the post request from Kitchen
                val rawOrd = call.receive<String>()
                //Deserialize Json into Finished Order object
                val finOrd = Json.decodeFromString(FinishedOrder.serializer(), rawOrd)
                println("Order ${finOrd.order_id} for ${finOrd.table_id} via ${finOrd.waiter_id} has arrived")
                //Add finished order to the list of all finished orders
                if(finOrd.table_id != null )finishedOrderList.put(finOrd.table_id!!,finOrd) else {
                    foodDeliveryFinishedOrder.put(finOrd.order_id, finOrd)
                    foodsInWaitingList.decrementAndGet()
                }
                //Answers the server with ok
                call.respondText("Okay", status= HttpStatusCode.Created)

            }
        }
    }.start(wait = false)
    var waiters = ArrayList<Waiter>()
    for (i in 1..rest.nr_of_waiters){
        var w = Waiter()
        w.setId(i)
        waiters.add(w)
    }
    for (i in 0.. rest.nr_of_tables - 1){
        tables.add(Table())
        tables[i].setTabId(i)
    }
    for (w in waiters) w.start()
    for(t in tables) t.start()
}
