package edu.harvard.cs50.notes;

import androidx.room.TypeConverter;

import java.util.Date;

/***
 * DateTypeConverter Class
 * TypeConverter for Room Database to convert Dates to Longs.
 * Longs are stored in Database as INTEGERS
 ***/
public class DateTypeConverter {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

}
