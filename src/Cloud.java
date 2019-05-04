import java.io.Serializable;

class Cloud implements Comparable<Cloud>, Serializable {
    private String nickname;
    private int speed; //приоритет по этому!!!!!!
    private String color;
    private int X;
    private int Y;
    private int Z;
    private MumyTroll rider;
    private static int cloudCount = 0;

    Cloud() {
        this("white", "Cloud" + cloudCount++, 10, "Troll");
    }

    Cloud(String color, String nickname, int speed, String trollName) {
        X = 0;
        Y = 0;
        Z = 0;
        this.color = color;
        this.nickname = nickname;
        this.speed = speed;
        rider = new MumyTroll(trollName);
    }

    Cloud(String nickname) {
        this("white", nickname, 10, "Troll");
    }

    Cloud(int speed) {
        this("white", "Cloud" + cloudCount++, speed, "Troll");
    }

    int getX() {
        return X;
    }

    int getY() {
        return Y;
    }

    int getZ() {
        return Z;
    }

    String getNickname() {
        return nickname;
    }

    String getColor() {
        return color;
    }

    MumyTroll getRider() {
        return rider;
    }

    void setRider(MumyTroll rider) {
        this.rider = rider;
    }

    void deleteRider() {
        rider = null;
    }

    int getSpeed() {
        return speed;
    }

    void setSpeed(int speed) {
        this.speed = speed;
    }

    void moveTo(int x, int y, int z) {
        X = x;
        Y = y;
        Z = z;
        System.out.println(String.format("Cloud %s moved to (%d, %d, %d)",
                nickname, X, Y, Z));
    }

    void moveOn(int dx, int dy, int dz) {
        X+=dx;
        Y+=dy;
        Z+=dz;
        System.out.println(String.format("Cloud %s moved on (%d, %d, %d) metres",
                nickname, X, Y, Z));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cloud cloud = (Cloud) obj;
        return (X==cloud.X && Y==cloud.Y && Z==cloud.Z && color.equals(cloud.color)
                && nickname.equals(cloud.nickname) && speed==cloud.speed);
    }

    @Override
    public String toString() {
        return "Cloud{" +
                "X=" + X +
                ", Y=" + Y +
                ", Z=" + Z +
                ", speed=" + speed +
                ", color='" + color + '\'' +
                ", nickname='" + nickname + '\'' +
                ", rider=" + rider +
                '}';
    }

    @Override
    public int compareTo(Cloud cloud) {
        return cloud.getSpeed() - speed;
    }
}
