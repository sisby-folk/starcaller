package folk.sisby.starcaller.util;

import folk.sisby.starcaller.StarcallerStar;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class StarUtil {
    public static List<StarcallerStar> generateStars(long seed) {
        Random random = Random.create(seed);
        List<StarcallerStar> list = new ArrayList<>();

        for(int i = 0; i < 1500; ++i) {
            double d = random.nextFloat() * 2.0F - 1.0F;
            double e = random.nextFloat() * 2.0F - 1.0F;
            double f = random.nextFloat() * 2.0F - 1.0F;
            double g = 0.15F + random.nextFloat() * 0.1F;
            double h = d * d + e * e + f * f;
            if (h < 1.0 && h > 0.01) {
                h = 1.0 / Math.sqrt(h);
                d *= h;
                e *= h;
                f *= h;
                double j = d * 100.0;
                double k = e * 100.0;
                double l = f * 100.0;
                double m = Math.atan2(d, f);
                double n = Math.sin(m);
                double o = Math.cos(m);
                double p = Math.atan2(Math.sqrt(d * d + f * f), e);
                double q = Math.sin(p);
                double r = Math.cos(p);
                double s = random.nextDouble() * Math.PI * 2.0;
                double t = Math.sin(s);
                double u = Math.cos(s);

                // Quad Stuff?
                double x = (double)(-1) * g;
                double y = (double)(-1) * g;
                double aa = x * u - y * t;
                double ab = y * u + x * t;
                double ad = aa * q + 0.0 * r;
                double ae = 0.0 * q - aa * r;
                double af = ae * n - ab * o;
                double ah = ab * n + ae * o;
                list.add(new StarcallerStar(StarNameUtil.STAR_NAMES.get(list.size()), j + af, k + ad, l + ah));
            }
        }
        return list;
    }
}
