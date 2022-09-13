package com.pr

import Constants
import pr.Order

class OrderManager:Thread() {
    var menu = Constants.getMenu()
    var tableList = arrayOfNulls<Order>(Constants.NR_OF_TABLES)
    override fun run() {
        while (true){
            if (null in tableList) {
                sleep(((3 * Constants.TIME_UNIT)..(10 * Constants.TIME_UNIT)).random().toLong())
                generateOrder(tableList.indexOf(null))
            }
        }
    }

    fun generateOrder(loc: Int){
        var foods = ArrayList<Int>()
        var maxTime = 0
        var maxFood = (1..Constants.NR_OF_MAXFOOD).random()
        for (i in 0..maxFood){
            val id = (1..13).random()
            foods.add(id)
            maxTime = if (menu[id - 1].preparationTime > maxTime) menu[id - 1].preparationTime else maxTime
        }
        maxTime = (maxTime * 1.3).toInt()

        var order = Order((1..10000).random(), foods, (1..5).random(), maxTime, pick_up_time = System.currentTimeMillis() ,table_id = loc )
        tableList[loc] = order
        println("Order for table $loc $foods")

    }


}