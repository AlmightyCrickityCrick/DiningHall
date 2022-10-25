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
        route("/v2") {
            post("/order") {
                var data = call.receive<String>()
                val ord = Json.decodeFromString(RestaurantOrder.serializer(), data)
                var id = (1..9999).random()
                var time = System.currentTimeMillis()
                var estim = getWaitingTime(ord.items)

                call.respond(
                    Json.encodeToString(
                        RestaurantOrderResponse.serializer(),
                        RestaurantOrderResponse(
                            rest.restaurant_id,
                            id,
                            estim,
                            ord.created_time,
                            time
                        )
                    )
                )
                println("Received order from client $data")
                sendOrderAuto(Order(id, ord.items, ord.priority, ord.max_wait, time, null, null))
                foodDeliveryFinishedOrder.put(id, FinishedFoodOrderingOrder(id, false, estim, ord.priority, ord.max_wait, ord.created_time, time, 0, 0, null ))
            }
            get("/order/{id}") {
                var ord :FinishedFoodOrderingOrder = call.parameters["id"]?.let { it1 ->
                    foodDeliveryFinishedOrder.get(
                        it1.toInt())
                }!!
                println("Client came for order ${ord.order_id}")
                if(ord.is_ready){
                    call.respond(Json.encodeToString(FinishedFoodOrderingOrder.serializer(), ord))
                    foodDeliveryFinishedOrder.remove(ord.order_id)
                } else {
                    ord.estimated_waiting_time = (ord.max_wait * 0.1).toInt()
                    call.respond(Json.encodeToString(FinishedFoodOrderingOrder.serializer(), ord))
                }

            }
            post("/rating") {
                var data = call.receive<String>()
                var rat = Json.decodeFromString(ClientRating.serializer(), data)
                println("Received Raating from Food Ordering $rat")
                var currRating = rating.addFoodOrderingrating(rat.rating)
                call.respond(Json.encodeToString(ClientRatingResponse.serializer(), ClientRatingResponse(rest.restaurant_id, currRating.first, currRating.second)))
            }
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
    for (i in items) if (Constants.MENU[i - 1].cookingApparatus == null) A+= Constants.MENU[i-1].preparationTime else C+= Constants.MENU[i-1].preparationTime

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

