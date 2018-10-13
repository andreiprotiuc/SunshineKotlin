package com.example.protiuc.sunshinekotlin.data.database

import android.arch.persistence.room.TypeConverter
import java.util.*

/**
 * {@link TypeConverter} for long to {@link Date}
 * <p>
 * This stores the date as a long in the database, but returns it as a {@link Date}
 */
class DateConverter {
        @TypeConverter
        fun toDate(timestamp: Long?): Date? {
            return if (timestamp == null) null else Date(timestamp)
        }

        @TypeConverter
        fun toTimestamp(date: Date?): Long? {
            return date?.time
        }
}