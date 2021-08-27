package com.FIT3170.HealthMonitor.database;


import java.util.Map;

/**
 * Fill a map with values, as long as they are not null
 */
public class MapFiller {
    private  Map<String, Object> map;

    MapFiller(Map<String, Object> map){
        this.map = map;
    }

    /**
     * Put a key value pair into a map if and only if the value is not null
     * @param key
     * @param o
     */
    void putIfNotNull(String key, Object o){
        if(o != null){
            this.map.put(key, o);
        }
    }
}
