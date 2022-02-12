package com.lavakumar.inmemorykvstore;

import java.util.Objects;

public final class KeyType {
    final String fieldName;
    final String fieldType;

    public KeyType(String fieldName, String fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyType keyType = (KeyType) o;
        return Objects.equals(fieldName, keyType.fieldName) && Objects.equals(fieldType, keyType.fieldType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, fieldType);
    }
}
