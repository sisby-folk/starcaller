package folk.sisby.starcaller;

public class StarcallerStar {
    public final double x;
    public final double y;
    public final double z;
    public boolean grounded;
    public int color;
    public String editor;

    public StarcallerStar(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "%s, %s, %s".formatted(Math.round(x * 100) / 100.0F, Math.round(y * 100) / 100.0F, Math.round(z * 100) / 100.0F);
    }
}
