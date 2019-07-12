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
                value = currentMap.get(key);
            }
        }

        return value;
    }

    public static String getString(String keyPath, Map<String, Object> map) {
        Object obj = getObject(keyPath, map);
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    public static Integer getInteger(String keyPath, Map<String, Object> map) {
        return Integer.parseInt(getString(keyPath, map));
    }
}
