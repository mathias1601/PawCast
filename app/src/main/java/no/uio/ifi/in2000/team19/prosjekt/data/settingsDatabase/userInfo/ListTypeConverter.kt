package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListTypeConverter {
    @TypeConverter
    fun fromString(value: String): List<DogType>? {
        val listType = object : TypeToken<List<DogType>>() {}.type
        return Gson().fromJson(value, listType)
    }
    @TypeConverter
    fun fromList(list: List<DogType>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}