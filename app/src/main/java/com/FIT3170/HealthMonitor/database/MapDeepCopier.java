package com.FIT3170.HealthMonitor.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class takes care of deep-copying a Map returned from firestore
 */
class MapDeepCopier {

    /**
     * Deep-copy a Map
     * @param origin the map to deepCopy
     * @return a copy of a map that is safe to change without affecting the origin
     */
    public static Map<String, Object> deepCopy(Map<String, Object> origin){

        Map<String, Object> copy = new HashMap<String, Object>();

        for(Map.Entry<String, Object> entry : origin.entrySet()){
            if(entry.getValue() instanceof  List){
                copy.put(entry.getKey(),  deepCopy((List<Object>)entry.getValue()));
            }else if (entry.getValue() instanceof Map){
                copy.put(entry.getKey(), deepCopy((Map<String, Object>)entry.getValue()));
            }else{
                copy.put(entry.getKey(), entry.getValue());
            }
        }

        return copy;
    }

    /**
     * Deep-copy a list
     * @param origin the list to deep-copy
     * @return a new copy of the origin that is safe to modify without affecting the origin
     */
    private static List<Object> deepCopy(List<Object> origin){
        List<Object> copy = new ArrayList<Object>();
        for(Object o : origin){
            if(o instanceof  List){
                copy.add(deepCopy((List<Object>)o));
            }else if (o instanceof Map){
                copy.add(deepCopy((Map<String, Object>)o));
            }else{
                copy.add(o);
            }
        }

        return copy;
    }
}
