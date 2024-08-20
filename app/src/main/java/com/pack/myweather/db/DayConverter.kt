import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pack.myweather.models.Day

class DayConverter {

    @TypeConverter
    fun fromDay(day: Day): String {
        return Gson().toJson(day)
    }

    @TypeConverter
    fun toDay(dayString: String): Day {
        val type = object : TypeToken<Day>() {}.type
        return Gson().fromJson(dayString, type)
    }
}
