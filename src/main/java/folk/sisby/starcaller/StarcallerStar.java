package folk.sisby.starcaller;

import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class StarcallerStar {
    public final Vec3d pos;
    public boolean grounded;
    public int color;
    public Text editor;

    public StarcallerStar(double x, double y, double z) {
        this.pos = new Vec3d(x, y, z);
    }

    public StarcallerStar(Vec3d pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "%s, %s, %s".formatted(Math.round(pos.x * 100) / 100.0F, Math.round(pos.y * 100) / 100.0F, Math.round(pos.z * 100) / 100.0F);
    }
}
