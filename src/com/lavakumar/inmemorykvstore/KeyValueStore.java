package com.lavakumar.inmemorykvstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class KeyValueStore {

    /*
    get(String key) => Should return the value (object with attributes and their values). Return null if key not present
    search(String attributeKey, String attributeValue) => Returns a list of keys that have the given attribute key, value pair.
    put(String key, List<Pair<String, String>> listOfAttributePairs) => Adds the key and the attributes to the key-value store. If the key already exists then the value is replaced.
    delete(String key) => Deletes the key, value pair from the store.
    keys() => Return a list of all the keys
     */

    private final Map<String, ValueObject> store = new ConcurrentHashMap<>();
    private final Map<String, Class<?>> attributeTypeRegistry = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public Map<String, Object> get(String key) {
        lock.readLock().lock();
        try {
            if (store.containsKey(key)) {
                return store.get(key).getAttributes();
            }
            return null;
        } finally {
          lock.readLock().unlock();
        }
    }


    public List<String> search(String attributeKey, String attributeValue) {
        lock.readLock().lock();
        try {
            List<String> matchingKeys = new ArrayList<>();
            for(Map.Entry<String ,ValueObject> entry: store.entrySet()) {
                Object value = entry.getValue().getAttributes().get(attributeKey);
                if(value != null && attributeValue.equals(value.toString())){
                    matchingKeys.add(entry.getKey());
                }
            }
            return matchingKeys;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void put(String key, List<Pair<String, String>> listOfAttributePairs) {
        lock.writeLock().lock();
        try {
            Map<String, Object> attributes = new HashMap<>();
            for (Pair<String, String> pair : listOfAttributePairs) {
                String attributeKey = pair.getK();
                String attributeValue = pair.getV();
                attributes.put(attributeKey, determineTypeOfAttributeValue(attributeKey, attributeValue));
            }

            store.put(key, new ValueObject(attributes));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delete(String key){
        lock.writeLock().lock();
        try {
            store.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<String> keys() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(store.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    private Object determineTypeOfAttributeValue(String attributeKey, String attributeValue) {
        // regular expression
        // try -> Integer.parseInt(attributeValue); , Double.parseDouble
        Object attributeTypeValue;
        if(attributeValue.matches("-?\\d+")) {
            attributeTypeValue =  Integer.parseInt(attributeValue);
        } else if(attributeValue.matches("-?\\d+\\.\\d+")) {
            attributeTypeValue =  Double.parseDouble(attributeValue);
        } else if ("true".equalsIgnoreCase(attributeValue) ||  "false".equalsIgnoreCase(attributeValue)) {
            attributeTypeValue =  Boolean.parseBoolean(attributeValue);
        } else {
            attributeTypeValue = attributeValue;
        }

        Class<?> currentClass = attributeTypeValue.getClass();
        Class<?>  instertedClass = attributeTypeRegistry.putIfAbsent(attributeKey, currentClass);

        if(instertedClass!= null && !instertedClass.equals(currentClass)) {
            throw new IllegalArgumentException(
                    "Attribute Key "+ attributeKey +" is conflict as already different type indexed for this. Expected is"+ instertedClass.getSimpleName() + "But got :"+ currentClass.getSimpleName()
            );
        }

        return attributeTypeValue;
    }

}
