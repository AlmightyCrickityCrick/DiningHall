package com.pr

import Constants
import kotlinx.serialization.json.Json
import io.ktor.client.*
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

class Waiter:Thread() {
    var waiter_id = 0
    var tablesWaiting = ArrayList<Int>()
    fun setId(id : Int){
        this.waiter_id = id
    }

    override fun run() {
        super.run()
        waitTables()
    }
    //Function to constantly interact with tables
    fun waitTables(){
        while(true) {
            //If waiter has taken order from a table checks if  it hasn't come from kitchen
            if (tablesWaiting.size != 0) checkOrderStatus()
            //Checks if there are no new orders in order manager
            var order = findOrder()
        }
    }
    //Goes through the tables with orders to check if there are no new orders
    fun findOrder() {
        //Locks the access of other Threads (Slow, must be updated)
        waiterLock.lock()
        for (i in 0..tables.size - 1) {
            //If table has order, check if it had a waiter assigned to it
            if (tables[i].tableState.get()== 1) {
                tables[i].tableState.set(2)
                tables[i].order?.waiter_id = waiter_id
                println("Waiter $waiter_id picked order for table $i")
                //If not then pick up order and be paired with the table number in tableMapping
                tablesWaiting.add(i)
                waiterLock.unlock()
                sleep((2 * Constants.TIME_UNIT..4 * Constants.TIME_UNIT).random().toLong())
                //If it finds order sleeps for 2-4 units and then sends order to kitchen
                tables[i].order?.pick_up_time= System.currentTimeMillis()
                tables[i].order?.let { sendOrder(it) }
                return
            }
        }
        waiterLock.unlock()
        return
    }
    //Check if no food for tables where order taken has arrived
    fun checkOrderStatus(){
        for (ord in tablesWaiting)
            if( finishedOrderList.containsKey(ord)){
                //If order arrived, serve it
                finishedOrderList?.get(ord)?.let { serveOrder(ord, it) }
                finishedOrderList.remove(ord)
                return
            }

    }
// Function to send order to Kitchen
    fun sendOrder(ord: Order){
    //  Turn into Json
        var serilizedOrder = Json.encodeToString(Order.serializer(), ord)
    //Create HTTP client
        val client = HttpClient()
        println("Trying to send ${ord.order_id} order")
    //Send order to kitchen(HttpClient requires that requests be done either in couroutine or suspend function)
        runBlocking {
            val job = launch {
                val resp: HttpResponse = client.post(Constants.KITCHEN_URL+"/order") {
                    setBody(serilizedOrder)
                }
            }}
        client.close()
    }

    //Serve the prepared order to the client and remove the order from the table-waiter map and the table itself.
    fun serveOrder(table: Int, ord: FinishedOrder){
        if (tables[table].order?.order_id  == ord.order_id) {
            ord.cooking_time = (((System.currentTimeMillis() - ord.pick_up_time).toInt())/Constants.TIME_UNIT).toLong()
            rating.addRating(ord.max_wait, ord.cooking_time)
            tablesWaiting.remove(table)
            println("Order ${ord.order_id} has been served in ${ord.cooking_time} t.u.")
            tables[table].order = null
            tables[table].tableState.set(0)

        }}
}