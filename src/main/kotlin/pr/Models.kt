package pr

import kotlinx.serialization.Serializable

data class Food(val id: Int, val name : String, val preparationTime : Int, val complexity: Int, val cookingApparatus: String? =null){}


@Serializable
data class Order (val order_id : Int, var items: ArrayList<Int>, var priority:Int, var max_wait : Int,
                  var pick_up_time : Long?  = null, var table_id: Int? = null, var waiter_id: Int? = null)

@Serializable
data class CookingDetail(val food_id:Int, val cook_id:Int)

@Serializable
data class FinishedOrder(val order_id : Int, var items: ArrayList<Int>, var priority:Int, var max_wait : Int, var pick_up_time : Long,
                         var cooking_time:Int, var table_id: Int, var waiter_id: Int, var cooking_details: ArrayList<CookingDetail>)