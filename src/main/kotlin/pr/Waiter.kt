package pr

import com.pr.OrderManager
import kotlinx.serialization.json.Json
import io.ktor.client.*
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*

class Waiter:Thread() {
    var waiter_id = 0
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
            if (waiter_id in tableMapping.values) checkOrderStatus()
            //Checks if there are no new orders in order manager
            var order = findOrder(manager)
            //If it finds order sleeps for 2-4 units and then sends order to kitchen
            if (order != null) {
                sleep((2 * Constants.TIME_UNIT..4 * Constants.TIME_UNIT).random().toLong())
                sendOrder(order)
            }
        }
    }
    //Goes through the tables with orders to check if there are no new orders
    fun findOrder(table: OrderManager): Order? {
        //Locks the access of other Threads (Slow, must be updated)
        waiterLock.lock()
        for (i in 0..table.tableList.size - 1) {
            // If the current table does not have order, continue
            if (table.tableList[i] == null) continue
            //If table has order, check if it had a waiter assigned to it
            if (table.tableList[i]?.waiter_id == null) {
                println("Waiter $waiter_id picked order for table $i")
                //If not then pick up order and be paired with the table number in tableMapping
                table.tableList[i]?.waiter_id = waiter_id
                tableMapping[i] = waiter_id
                waiterLock.unlock()
                return table.tableList[i]
            }
        }
        if(waiterLock.isLocked)waiterLock.unlock()
        return null
    }
    //Check if no food for tables where order taken has arrived
    fun checkOrderStatus(){
        servingLock.lock()
        var finishedListCopy = finishedOrderList
        for (ord in finishedListCopy)
            if( waiter_id == ord.waiter_id){
                finishedOrderList.remove(ord)
                //If order arrived, serve it
                serveOrder(ord.table_id, ord)
                break
            }
        servingLock.unlock()

    }
// Function to send order to Kitchen
    fun sendOrder(ord:Order){
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
        if (manager.tableList[table]!!.order_id  == ord.order_id) {
            ord.cooking_time = (System.currentTimeMillis() - ord.pick_up_time).toInt()
            println("Order ${ord.order_id} has been served")
            manager.tableList[table] = null
            tableMapping.remove(table)

        }}
}