package com.pr

import kotlinx.serialization.Serializable

@Serializable
data class Self(var nr_of_tables: Int,
    var nr_of_waiters: Int,
var max_foods: Int,
var time_unit: Int,
var kitchen_url: String,
var dining_url : String,
var dining_port: Int,
var kitchen_port: Int,
var kitchen_ordering: String,
var restaurant_name: String,
var restaurant_id : Int,
var food_ordering_url: String,
var cook_prof: Int,
var cook_ap:Int)
@Serializable
data class Food(val id: Int, val name : String, val preparationTime : Int, val complexity: Int, val cookingApparatus: String? =null){}


@Serializable
data class Order (val order_id : Int, var items: ArrayList<Int>, var priority:Int, var max_wait : Int,
                  var pick_up_time : Long?  = null, var table_id: Int? = null, var waiter_id: Int? = null)

@Serializable
data class CookingDetail(val food_id:Int, val cook_id:Int? = null)

@Serializable
data class FinishedOrder(val order_id : Int, var items: ArrayList<Int>, var priority:Int, var max_wait : Int, var pick_up_time : Long,
                         var cooking_time:Long, var table_id:Int?=null, var waiter_id: Int?=null, var cooking_details: ArrayList<CookingDetail>)

@Serializable
data class Restaurant(val restaurant_id:Int, val name:String, val address:String, val menuItems:Int, val menu:ArrayList<Food>, var rating: Float)
@Serializable
data class RestaurantOrder(val items: ArrayList<Int>, val priority: Int, val max_wait: Int, val created_time: Long)

@Serializable
data class RestaurantOrderResponse(val restaurant_id: Int, val order_id: Int, val estimated_waiting_time: Int, val created_time: Long, val registered_time: Long)

@Serializable
data class MenuResource(val foods : ArrayList<Food>)

//Response when client checks if order ready
@Serializable
data class FinishedFoodOrderingOrder(val order_id:Int, var is_ready: Boolean, var estimated_waiting_time: Int, val priority: Int, val max_wait: Int, val created_time: Long, val registered_time: Long, var preparedTime:Long, var cooking_time: Long, var cooking_details: ArrayList<CookingDetail>?=null)

//Request from client to rate restaurant
@Serializable
data class ClientRating(val order_id: Int, val rating: Int, val estimated_waiting_time: Int, val waiting_time:Int)

//Response to client after rating restaurant
@Serializable
data class ClientRatingResponse(val restaurant_id: Int, val restaurant_avg_rating:Float, var prepared_orders:Int)