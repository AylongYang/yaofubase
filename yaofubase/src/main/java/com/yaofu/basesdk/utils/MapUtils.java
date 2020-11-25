package com.yaofu.basesdk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Map Utils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2011-7-22
 */
public class MapUtils {

    /**
     * default separator between key and value *
     */
    public static final String DEFAULT_KEY_AND_VALUE_SEPARATOR = ":";
    /**
     * default separator between key-value pairs *
     */
    public static final String DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR = ",";

    /**
     * is null or its size is 0
     *
     * <pre>
     * isEmpty(null)   =   true;
     * isEmpty({})     =   true;
     * isEmpty({1, 2})    =   false;
     * </pre>
     *
     * @return if map is null or its size is 0, return true, else return false.
     */
    public static <K, V> boolean isEmpty(Map<K, V> sourceMap) {
        return (sourceMap == null || sourceMap.size() == 0);
    }

    /**
     * add key-value pair to map, and key need not null or empty
     *
     * @return <ul>
     * <li>if map is null, return false
     * <li>if key is null or empty, return false
     * <li>return {@link Map#put(Object, Object)}
     * </ul>
     */
    public static boolean putMapNotEmptyKey(Map<String, String> map, String key, String value) {
        if (map == null || StringUtils.isEmpty(key)) {
            return false;
        }

        map.put(key, value);
        return true;
    }

    /**
     * add key-value pair to map, both key and value need not null or empty
     *
     * @return <ul>
     * <li>if map is null, return false
     * <li>if key is null or empty, return false
     * <li>if value is null or empty, return false
     * <li>return {@link Map#put(Object, Object)}
     * </ul>
     */
    public static boolean putMapNotEmptyKeyAndValue(Map<String, String> map, String key,
                                                    String value) {
        if (map == null || StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return false;
        }

        map.put(key, value);
        return true;
    }

    /**
     * add key-value pair to map, key need not null or empty
     *
     * @return <ul>
     * <li>if map is null, return false
     * <li>if key is null or empty, return false
     * <li>if value is null or empty, put defaultValue, return true
     * <li>if value is neither null nor empty，put value, return true
     * </ul>
     */
    public static boolean putMapNotEmptyKeyAndValue(Map<String, String> map, String key, String value,
                                                    String defaultValue) {
        if (map == null || StringUtils.isEmpty(key)) {
            return false;
        }

        map.put(key, StringUtils.isEmpty(value) ? defaultValue : value);
        return true;
    }

    /**
     * add key-value pair to map, key need not null
     *
     * @return <ul>
     * <li>if map is null, return false
     * <li>if key is null, return false
     * <li>return {@link Map#put(Object, Object)}
     * </ul>
     */
    public static <K, V> boolean putMapNotNullKey(Map<K, V> map, K key, V value) {
        if (map == null || key == null) {
            return false;
        }

        map.put(key, value);
        return true;
    }

    /**
     * add key-value pair to map, both key and value need not null
     *
     * @return <ul>
     * <li>if map is null, return false
     * <li>if key is null, return false
     * <li>if value is null, return false
     * <li>return {@link Map#put(Object, Object)}
     * </ul>
     */
    public static <K, V> boolean putMapNotNullKeyAndValue(Map<K, V> map, K key, V value) {
        if (map == null || key == null || value == null) {
            return false;
        }

        map.put(key, value);
        return true;
    }

    /**
     * parse key-value pairs to map, ignore empty key
     *
     * <pre>
     * parseKeyAndValueToMap("","","",true)=null
     * parseKeyAndValueToMap(null,"","",true)=null
     * parseKeyAndValueToMap("a:b,:","","",true)={(a,b)}
     * parseKeyAndValueToMap("a:b,:d","","",true)={(a,b)}
     * parseKeyAndValueToMap("a:b,c:d","","",true)={(a,b),(c,d)}
     * parseKeyAndValueToMap("a=b, c = d","=",",",true)={(a,b),(c,d)}
     * parseKeyAndValueToMap("a=b, c = d","=",",",false)={(a, b),( c , d)}
     * parseKeyAndValueToMap("a=b, c=d","=", ",", false)={(a,b),( c,d)}
     * parseKeyAndValueToMap("a=b; c=d","=", ";", false)={(a,b),( c,d)}
     * parseKeyAndValueToMap("a=b, c=d", ",", ";", false)={(a=b, c=d)}
     * </pre>
     *
     * @param source                   key-value pairs
     * @param keyAndValueSeparator     separator between key and value
     * @param keyAndValuePairSeparator separator between key-value pairs
     * @param ignoreSpace              whether ignore space at the begging or end of key and value
     */
    public static Map<String, String> parseKeyAndValueToMap(String source,
                                                            String keyAndValueSeparator, String keyAndValuePairSeparator, boolean ignoreSpace) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }

        if (StringUtils.isEmpty(keyAndValueSeparator)) {
            keyAndValueSeparator = DEFAULT_KEY_AND_VALUE_SEPARATOR;
        }
        if (StringUtils.isEmpty(keyAndValuePairSeparator)) {
            keyAndValuePairSeparator = DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR;
        }
        Map<String, String> keyAndValueMap = new HashMap<String, String>();
        String[] keyAndValueArray = source.split(keyAndValuePairSeparator);
        if (keyAndValueArray == null) {
            return null;
        }

        int seperator;
        for (String valueEntity : keyAndValueArray) {
            if (!StringUtils.isEmpty(valueEntity)) {
                seperator = valueEntity.indexOf(keyAndValueSeparator);
                if (seperator != -1) {
                    if (ignoreSpace) {
                        MapUtils.putMapNotEmptyKey(keyAndValueMap, valueEntity.substring(0, seperator).trim(),
                                valueEntity.substring(seperator + 1).trim());
                    } else {
                        MapUtils.putMapNotEmptyKey(keyAndValueMap, valueEntity.substring(0, seperator),
                                valueEntity.substring(seperator + 1));
                    }
                }
            }
        }
        return keyAndValueMap;
    }

    /**
     * parse key-value pairs to map, ignore empty key
     *
     * @param source      key-value pairs
     * @param ignoreSpace whether ignore space at the begging or end of key and value
     * @see {@link MapUtils#parseKeyAndValueToMap(String, String, String, boolean)},
     * keyAndValueSeparator is {@link #DEFAULT_KEY_AND_VALUE_SEPARATOR}, keyAndValuePairSeparator
     * is {@link #DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR}
     */
    public static Map<String, String> parseKeyAndValueToMap(String source, boolean ignoreSpace) {
        return parseKeyAndValueToMap(source, DEFAULT_KEY_AND_VALUE_SEPARATOR,
                DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR, ignoreSpace);
    }

    /**
     * parse key-value pairs to map, ignore empty key, ignore space at the begging or end of key and
     * value
     *
     * @param source key-value pairs
     * @see {@link MapUtils#parseKeyAndValueToMap(String, String, String, boolean)},
     * keyAndValueSeparator is {@link #DEFAULT_KEY_AND_VALUE_SEPARATOR}, keyAndValuePairSeparator
     * is {@link #DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR}, ignoreSpace is true
     */
    public static Map<String, String> parseKeyAndValueToMap(String source) {
        return parseKeyAndValueToMap(source, DEFAULT_KEY_AND_VALUE_SEPARATOR,
                DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR, true);
    }

    /**
     * join map
     */
    public static String toJson(Map<String, String> map) {
        if (map == null || map.size() == 0) {
            return null;
        }

        StringBuilder paras = new StringBuilder();
        paras.append("{");
        Iterator<Entry<String, String>> ite = map.entrySet().iterator();
        while (ite.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) ite.next();
            paras.append("\"")
                    .append(entry.getKey())
                    .append("\":\"")
                    .append(entry.getValue())
                    .append("\"");
            if (ite.hasNext()) {
                paras.append(",");
            }
        }
        paras.append("}");
        return paras.toString();
    }

    //map的value值转list
    public static List mapTransitionValueList(Map map) {
        List list = new ArrayList();
        Iterator iter = map.entrySet().iterator(); //获得map的Iterator
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            list.add(entry.getValue());
        }
        return list;
    }

    //map的key值转list
    public static List mapTransitionKeyList(Map map) {
        List list = new ArrayList();
        Iterator iter = map.entrySet().iterator(); //获得map的Iterator
        while (iter.hasNext()) {
            Entry entry = (Entry) iter.next();
            list.add(entry.getKey());
        }
        return list;
    }

    public static String getKey(HashMap<String, String> map, String value) {
        String key = null;
        //Map,HashMap并没有实现Iteratable接口.不能用于增强for循环.
        for (String getKey : map.keySet()) {
            if (map.get(getKey).equals(value)) {
                key = getKey;
            }
        }
        return key;
        //这个key肯定是最后一个满足该条件的key.
    }

    public static List<String> getKeyList(HashMap<String, String> map, String value) {
        List<String> keyList = new ArrayList();
        for (String getKey : map.keySet()) {
            if (map.get(getKey).equals(value)) {
                keyList.add(getKey);
            }
        }
        return keyList;
    }
}
