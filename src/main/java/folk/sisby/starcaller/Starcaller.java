package folk.sisby.starcaller;

import folk.sisby.starcaller.entity.JavelinEntity;
import folk.sisby.starcaller.item.JavelinItem;
import folk.sisby.starcaller.item.StardustItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Starcaller implements ModInitializer {
    public static final String ID = "starcaller";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
    public static final List<Vec3d> STAR_POS = getStars();

    public static final StardustItem STARDUST = Registry.register(Registries.ITEM, new Identifier(ID, "stardust"), new StardustItem(new FabricItemSettings().maxCount(1)));
    public static final JavelinItem JAVELIN = Registry.register(Registries.ITEM, new Identifier(ID, "javelin"), new JavelinItem(new FabricItemSettings().maxCount(1)));
    public static final EntityType<JavelinEntity> JAVELIN_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(ID, "javelin"),
            FabricEntityTypeBuilder.<JavelinEntity>create(SpawnGroup.MISC, JavelinEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                    .trackRangeChunks(4)
                    .trackedUpdateRate(20)
                    .build()
    );

    @Override
    public void onInitialize() {
        LOGGER.info("[Starcaller] Initialized.");
    }

    private static List<Vec3d> getStars() {
        Random random = Random.create(10842L);
        List<Vec3d> list = new ArrayList<>();

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

                for(int v = 0; v < 4; ++v) {
                    double x = (double)((v & 2) - 1) * g;
                    double y = (double)((v + 1 & 2) - 1) * g;
                    double aa = x * u - y * t;
                    double ab = y * u + x * t;
                    double ad = aa * q + 0.0 * r;
                    double ae = 0.0 * q - aa * r;
                    double af = ae * n - ab * o;
                    double ah = ab * n + ae * o;
                    list.add(new Vec3d(j + af, k + ad, l + ah));
                }
            }
        }
        return list;
    }
}
