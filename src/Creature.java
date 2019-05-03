import java.io.Serializable;
import java.util.Objects;

abstract class Creature implements Serializable {

    protected String name;
    protected String location;
    protected String mood;

    Creature(String name, String location, String mood) {
        this.name = name;
        this.location = location;
        this.mood = mood;
    }

    String getName() {
        return name;
    }

    String getLocation() {
        return location;
    }

    String getMood() {
        return mood;
    }

    void setLocation(String location) {
        this.location = location;
    }

    void setMood(String mood) {
        this.mood = mood;
    }

    abstract void think(String theme);

    abstract void walk(String place);

    void say(String saying) {
        System.out.println(name+" said: '"+saying+"'");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Creature creature = (Creature) o;
        return (name.equals(creature.name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
