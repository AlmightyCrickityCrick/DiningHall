package com.pr.plugins

import Constants
import com.pr.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    routing {
        post("/v2/order") {
            var data = call.receive<String>()
            val ord = Json.decodeFromString(RestaurantOrder.serializer(), data)
            var id = (1..9999).random()
            var time = System.currentTimeMillis()
            call.respond(Json.encodeToString(RestaurantOrderResponse.serializer(), RestaurantOrderResponse(rest.restaurant_id, id, getWaitingTime(ord.items), ord.created_time, time)))
            println("Received order from client $data")
            sendOrderAuto(Order(id, ord.items, ord.priority, ord.max_wait, time, null, null))
        }
    }
}

fun getWaitingTime(items:ArrayList<Int>):Int{
    var A:Int = 0
    var B = rest.cook_prof
    var C:Int = 0
    var D = rest.cook_ap
    var E = foodsInWaitingList.get()
    var F = items.size
    foodsInWaitingList.incrementAndGet()
    for (i in items) if (Constants.MENU[i - 1].cookingApparatus == null) A++ else C++

    println(("Expected time for order, " +( A/B+C/D) * (E+F)/F))
    return (A/B+C/D) * (E+F)/F
/// (A/B + C/D) *(E+F)/F
    // A - total time of food that doesnt require cooking apparatus, B - Sum of Cook Prof,  C - Total Time food with cook apparatus
    // D - cooking apparatus nr, E - foods in waiting list , F - nr foods in current order
}

fun sendOrderAuto(ord: Order){
    //  Turn into Json
    var serilizedOrder = Json.encodeToString(Order.serializer(), ord)
    //Create HTTP client
    val client = HttpClient()
    println("Trying to send ${ord.order_id} order from Food Service")
    //Send order to kitchen(HttpClient requires that requests be done either in couroutine or suspend function)
    runBlocking {
        val job = launch {
            val resp: HttpResponse = client.post(rest.kitchen_url+"/order") {
                setBody(serilizedOrder)
            }
        }}
    client.close()
}

