package folk.sisby.starcaller;

public class StarcallerStar {
    public final String name;
    public final double x;
    public final double y;
    public final double z;
    public boolean grounded;
    public int color;
    public String editor;

    public StarcallerStar(String name, double x, double y, double z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "Star %s at %s, %s, %s".formatted(name, x, y, z);
    }
}
