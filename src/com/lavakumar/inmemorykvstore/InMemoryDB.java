package com.lavakumar.inmemorykvstore;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryDB<K,V> {
    Map<K,V> map=new HashMap<>();
    Set<KeyType> keyTypes = new HashSet<>();
    public void put(K key, V value) {
        try {
            Class<?> valueClass = value.getClass();
            if(valueClass.getName().contains(ValueType.MAP.toString())) {
                Map<String, Object> valueMap = (Map<String, Object>) value;
                for(KeyType keyType: keyTypes){
                    if (!valueMap.get(keyType.getFieldName()).getClass().getName().equals(keyType.getFieldType())) {
                        throw new UnsupportedOperationException("Data Type Error");
                    }
                }
                for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                    keyTypes.add(new KeyType(entry.getKey(),entry.getValue().getClass().getName()));
                }

            } else if(valueClass.getName().contains(ValueType.OBJECT.toString())){
                Object valueMap = value;
//                for(KeyType keyType: keyTypes){
//                    if (!valueMap.get(keyType.getFieldName()).getClass().getName().equals(keyType.getFieldType())) {
//                        throw new UnsupportedOperationException("Data Type Error");
//                    }
//                }
//                for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
//                    keyTypes.add(new KeyType(entry.getKey(),entry.getValue().getClass().getName()));
//                }
            }
            map.put(key, value);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public Object get(K key){
       V v = map.get(key);
       if(v == null){
           return "No Entry Found For key "+key;
       }
       return v;
    }

    public List<String> search(String attributeKey, String attributeValue) {
           List<String> keys = new ArrayList<>();
           for(Map.Entry<K, V> entry: map.entrySet()){
               V value = entry.getValue();
             //  if(value.getClass().equals())
           }

           return keys;
    }

    @Override
    public String toString() {
        return "InMemoryDB{" +
                "map=" + map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}")) +
                '}';
    }
}
