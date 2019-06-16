package com.jrestats.util;

import java.util.List;
import java.util.Map;

public class DataUtil {

    public static Map<String, Object> getMap(String key, Map<String, Object> map) {
        return (Map<String, Object>) map.get(key);
    }

    public static List<Map<String, Object>> getList(String key, Map<String, Object> map) {
        return (List<Map<String, Object>>) map.get(key);
    }

    public static Object getObject(String keyPath, Map<String, Object> map) {
        Object value = null;

        Map<String, Object> currentMap = map;
        for (String key : keyPath.split("\\.")) {
            try {
                currentMap = getMap(key, currentMap);
            } catch (ClassCastException e) {
                value = (Object) currentMap.get(key);
            }
        }

        return value;
    }

    public static String getString(String keyPath, Map<String, Object> map) {
        return (String) getObject(keyPath, map);
    }

    public static Integer getInteger(String keyPath, Map<String, Object> map) {
        return (Integer) getObject(keyPath, map);
    }
}
