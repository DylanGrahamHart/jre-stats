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

    public static Integer getIntegerFromMap(String keyPath, Map<String, Object> map) {
        Integer value = null;

        Map<String, Object> currentMap = map;
        for (String key : keyPath.split("\\.")) {
            try {
                currentMap = getMap(key, currentMap);
            } catch (ClassCastException e) {
                value = (Integer) currentMap.get(key);
            }
        }

        return value;
    }
}
