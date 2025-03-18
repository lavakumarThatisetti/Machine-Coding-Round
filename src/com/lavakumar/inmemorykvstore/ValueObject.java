package com.lavakumar.inmemorykvstore;

import java.util.HashMap;
import java.util.Map;

public class ValueObject {
    private final Map<String, Object> attributes;

    public ValueObject(Map<String, Object> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Object> entry: attributes.entrySet()) {
            sb.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
        }
        return sb.toString();
    }
}
