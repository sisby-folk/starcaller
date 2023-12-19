package folk.sisby.starcaller.util;

import folk.sisby.starcaller.StarcallerStar;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class StarUtil {
    public static List<StarcallerStar> generateStars(long seed) {
        Random random = Random.create(seed);
        List<StarcallerStar> list = new ArrayList<>();

        for (int i = 0; i < 1500; ++i) {
            double d = random.nextFloat() * 2.0F - 1.0F;
            double e = random.nextFloat() * 2.0F - 1.0F;
            double f = random.nextFloat() * 2.0F - 1.0F;
            double r2 = d * d + e * e + f * f;
            if (r2 < 1.0 && r2 > 0.01) {
                double ir2 = 1.0 / Math.sqrt(r2);
                d *= ir2;
                e *= ir2;
                f *= ir2;
                list.add(new StarcallerStar(f * 100.0, e * 100.0, d * 100.0));
            }
        }
        list.add(new StarcallerStar(100.0F, 0, 0));
        list.add(new StarcallerStar(-100.0F, 0, 0));
        list.add(new StarcallerStar(0, 100.0F, 0));
        list.add(new StarcallerStar(0, -100.0F, 0));
        list.add(new StarcallerStar(0, 0, 100.0F));
        list.add(new StarcallerStar(0, 0, -100.0F));
        list.add(new StarcallerStar(0, 70.0F, 70.0F));
        list.add(new StarcallerStar(0, -70.0F, -70.0F));
        return list;
    }

    public static Vec3d getStarCursor(float yawDegrees, float pitchDegrees) {
        double azimuth = (yawDegrees) * Math.PI / 180;
        double inclination = (90.0F + pitchDegrees) * Math.PI / 180;
        return new Vec3d(-1 * Math.sin(azimuth) * Math.sin(inclination), Math.cos(inclination), Math.cos(azimuth) * Math.sin(inclination)).multiply(100.0F);
    }

    public static Vec3d correctForSkyAngle(Vec3d starCursor, float skyAngle) {
        double skyboxRotation = (1.0F - skyAngle) * 2 * Math.PI;
        return new Vec3d(
                Math.cos(skyboxRotation) * starCursor.x - Math.sin(skyboxRotation) * starCursor.y,
                Math.sin(skyboxRotation) * starCursor.x + Math.cos(skyboxRotation) * starCursor.y,
                starCursor.z
        );
    }
}