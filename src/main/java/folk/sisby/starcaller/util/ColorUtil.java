package folk.sisby.starcaller.util;

import org.joml.Vector3f;

public class ColorUtil {
    public static Vector3f colorToComponents(int color) {
        int j = (color & 0xFF0000) >> 16;
        int k = (color & 0xFF00) >> 8;
        int l = (color & 0xFF);
        return new Vector3f((float) j / 255.0F, (float) k / 255.0F, (float) l / 255.0F);
    }
}
