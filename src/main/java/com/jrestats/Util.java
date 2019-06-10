package com.jrestats;

import java.util.List;
import java.util.Map;

public class Util {

    public static Object getObject(String key, Map<String, Object> map) {
        return (Object) map.get(key);
    }

    public static Map<String, Object> getMap(String key, Map<String, Object> map) {
        return (Map<String, Object>) map.get(key);
    }

    public static List<Map<String, Object>> getList(String key, Map<String, Object> map) {
        return (List<Map<String, Object>>) map.get(key);
    }

    public static String getStringFromMap(String keyPath, Map<String, Object> map) {
        String value = null;

        Map<String, Object> currentMap = map;
        for (String key : keyPath.split("\\.")) {
            try {
                currentMap = getMap(key, currentMap);
            } catch (ClassCastException e) {
                value = (String) currentMap.get(key);
            }
        }

        return value;
    }
}
