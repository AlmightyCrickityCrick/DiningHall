import com.pr.Food
import com.pr.MenuResource
import com.pr.Self
import com.pr.rest
import kotlinx.serialization.json.Json
import java.io.File

object Constants{
    var NR_OF_TABLES = 10
    var NR_OF_WAITERS = 4
    var NR_OF_MAXFOOD = 5
    var TIME_UNIT = 1000
    //val KITCHEN_URL = "http://kitchen-container:8081"
    //val KITCHEN_URL = "http://kitchen-container1:8083"
    //var KITCHEN_URL = ""

    val MENU = getMenu()


    fun getMenu():ArrayList<Food>{
        var conf = File("config/menu.json").inputStream().readBytes().toString(Charsets.UTF_8)
        var foods= Json{coerceInputValues = true}.decodeFromString(MenuResource.serializer(), conf).foods

        print(foods)
        return foods

    }
}