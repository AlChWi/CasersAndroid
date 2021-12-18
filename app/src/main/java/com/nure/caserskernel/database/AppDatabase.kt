package com.nure.caserskernel.database

import android.content.Context
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.nure.caserskernel.database.entities.*
import java.util.*

@Database(
    entities = [
        CarEntity::class,
        CarTrailerEntity::class,
        SealedCargoEntity::class,
        CarServiceCallEntity::class
               ],
    version = 1
)
@TypeConverters(Converters::class)
public abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): DAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kernel_app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

object Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return if (date == null) null else date.getTime()
    }

    @TypeConverter
    fun toServiceCallType(value: String) = enumValueOf<ServiceCallType>(value)

    @TypeConverter
    fun fromServiceCallType(value: ServiceCallType) = value.name

    @TypeConverter
    fun stringToMap(value: String): Map<String, String> {
        return Gson().fromJson(value,  object : TypeToken<Map<String, String>>() {}.type)
    }

    @TypeConverter
    fun mapToString(value: Map<String, String>): String {
        return Gson().toJson(value)
    }
}