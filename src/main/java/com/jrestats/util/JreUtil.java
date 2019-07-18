package com.jrestats.util;

import java.util.List;
import java.util.Map;

public class JreUtil {

    public static Map<String, Object> getMap(String key, Map<String, Object> map) {
        return (Map<String, Object>) map.get(key);
    }

    public static List<Map<String, Object>> getList(String key, Map<String, Object> map) {
        return (List<Map<String, Object>>) map.get(key);
    }

}
