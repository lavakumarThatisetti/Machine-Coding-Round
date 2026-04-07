package model;

public class Rider {
    private final String id;
    private final String name;
    private volatile Location currentLocation;

    public Rider(String id, String name, Location currentLocation) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void updateLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}
