import java.io.Serializable;
import java.util.Objects;

final class MumyTroll implements Serializable {
    private String name;
    private String location;
    private String mood;

    MumyTroll(String name, String location, String mood) {
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

    void say(String saying) {
        System.out.println(name + " said: '" + saying + "'");
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MumyTroll creature = (MumyTroll) o;
        return (name.equals(creature.name));
    }

    public int hashCode() {
        return Objects.hash(name);
    }

    public String toString() {
        return name;
    }

    MumyTroll(String name) {
        this(name, "forest", "normal");
        //System.out.println(name+" appears in "+location);
    }

    void think(String theme) {
        System.out.println(this.name + " is thinking about " + theme);
    }

    void walk(String place) {
        location = place;
        System.out.println(name+" is "+mood+"ly walking around "+location);
    }
}
