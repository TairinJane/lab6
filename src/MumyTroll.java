import java.io.Serializable;

final class MumyTroll extends Creature implements Serializable {

    MumyTroll(String name) {
        super(name, "forest", "normal");
        //System.out.println(name+" appears in "+location);
    }

    @Override
    void think(String theme) {
        System.out.println(this.name + " is thinking about " + theme);
    }

    @Override
    void walk(String place) {
        location = place;
        System.out.println(name+" is "+mood+"ly walking around "+location);
    }
}
