import com.pr.Food

object Constants{
    var NR_OF_TABLES = 10
    var NR_OF_WAITERS = 4
    var NR_OF_MAXFOOD = 5
    var TIME_UNIT = 1000
    //val KITCHEN_URL = "http://kitchen-container:8081"
    val KITCHEN_URL = "http://kitchen-container1:8083"
    //var KITCHEN_URL = ""

    val MENU = getMenu()


    fun getMenu():ArrayList<Food>{
        var foods= ArrayList<Food>()
        foods.add(
            Food(1, "pizza", 20, 2, "oven" )
        )

        foods.add(
            Food(2, "salad", 10, 1, null )
        )
        foods.add(
            Food(3, "zeama", 7, 1, "stove" )
        )
        foods.add(
            Food(4, "Scallop Sashimi", 32, 3, null )
        )
        foods.add(
            Food(5, "Island Duck", 35, 3, "oven" )
        )
        foods.add(
            Food(6, "Waffles", 10, 1, "stove" )
        )
        foods.add(
            Food(7, "Aubergine", 20, 2, "oven" )
        )
        foods.add(
            Food(8, "Lasagna", 30, 2, "oven" )
        )
        foods.add(
            Food(9, "Burger", 15, 1, "stove" )
        )
        foods.add(
            Food(10, "Gyros", 15, 1, null )
        )
        foods.add(
            Food(11, "Kebab", 15, 1, null )
        )
        foods.add(
            Food(12, "Unagi Maki", 20, 2, null )
        )
        foods.add(
            Food(13, "Tabacco Chicken", 30, 2, "oven" )
        )

        return foods

    }
}