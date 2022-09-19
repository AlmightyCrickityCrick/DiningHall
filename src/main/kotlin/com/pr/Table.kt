package com.pr

import Constants
import java.util.concurrent.atomic.AtomicInteger

class Table:Thread() {
    var tableId = 0
    var order : Order? = null
    var tableState = AtomicInteger(0) //0 - order not ready, 1 - order ready to be taken, 2 - awaiting order
    override fun run() {
        while (true){
            if(tableState.get() == 0){
                sleep(((3 * Constants.TIME_UNIT)..(10 * Constants.TIME_UNIT)).random().toLong())
                generateOrder()
                tableState.set(1)
            }
            else if (tableState.get() == 1) {
            }
            else{

            }
        }
    }

    fun setTabId(id:Int){
        this.tableId = id
    }

    fun generateOrder(){
        var foods = ArrayList<Int>()
        var maxTime = 0
        var totalTime = 0
        var maxFood = (1..Constants.NR_OF_MAXFOOD - 1).random()
        for (i in 0..maxFood){
            val id = (1..13).random()
            foods.add(id)
            totalTime += Constants.MENU[id - 1].preparationTime
            maxTime = if (Constants.MENU[id - 1].preparationTime > maxTime) Constants.MENU[id - 1].preparationTime else maxTime
        }
        maxTime = (maxTime * 1.3).toInt()
        priority = setPriority(totalTime, foods.size, maxTime)

        var order = Order((1..10000).random(), foods, priority, maxTime, pick_up_time = System.currentTimeMillis() ,table_id = this.tableId )
        this.order = order
        println("Order for table $tableId $foods id ${order.order_id}")

    }

    fun setPriority(total:Int, items:Int, max:Int ):Int{
        println("total $total items $items max $max total/items ${total/items}")
        if(items<=2) return 1
        else if (items<=4) return 2
        else if (items <= 6) return 3
        else if (items<=8) return 4
        else return 5
    }


}