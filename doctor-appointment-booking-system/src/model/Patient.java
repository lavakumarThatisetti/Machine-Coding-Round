package model;

public class Patient {
    private final String id;
    private final String name;

    public Patient(String id, String name) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Patient id cannot be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Patient name cannot be blank");
        }
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}