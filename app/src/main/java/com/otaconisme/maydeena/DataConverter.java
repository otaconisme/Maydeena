package com.otaconisme.maydeena;

import android.arch.persistence.room.TypeConverter;

import java.util.UUID;

/**
 * Created by Zakwan on 12/19/2017.
 * Converter UUID --> String, String --> UUID
 */

public class DataConverter {
    @TypeConverter
    public static String toString(UUID id){
        return id == null ? null : id.toString();
    }

    @TypeConverter
    public static UUID toUUID(String id){
        return id == null ? null : UUID.fromString(id);
    }

}
