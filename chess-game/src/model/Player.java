package model;

public class Player {
    private final String id;
    private final String name;
    private final Color color;

    public Player(String id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }
}
