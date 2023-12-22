package folk.sisby.starcaller;

import folk.sisby.starcaller.duck.StarcallerWorld;
import folk.sisby.starcaller.item.SpearItem;
import folk.sisby.starcaller.item.StardustItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starcaller implements ModInitializer {
    public static final String ID = "starcaller";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
    public static final boolean DEBUG_SKY = false;
    public static final long STAR_SEED = 10842L;
    public static final int STAR_ITERATIONS = 1500;
    public static final int STAR_GROUNDED_TICKS = 1200;
    public static final String STATE_KEY = "starcaller_stars";

    public static final StardustItem STARDUST = Registry.register(Registries.ITEM, id("stardust"), new StardustItem(new FabricItemSettings().maxCount(1)));
    public static final SpearItem SPEAR = Registry.register(Registries.ITEM, id("spear"), new SpearItem(new FabricItemSettings().maxCount(1)));

    @Override
    public void onInitialize() {
        ServerWorldEvents.LOAD.register(((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                StarState state = world.getPersistentStateManager().getOrCreate(StarState.getPersistentStateType(), STATE_KEY);
                if (Starcaller.DEBUG_SKY) {
                    LOGGER.info("[Starcaller] Start Logging World Stars");
                    state.stars.forEach(s -> LOGGER.info("[Starcaller] {}", s));
                    LOGGER.info("[Starcaller] End Logging World Stars");
                }
            }
        }));
        ServerTickEvents.END_WORLD_TICK.register((world -> {
            for (Star star : ((StarcallerWorld) world).starcaller$getStars()) {
                if (world.getTime() > star.groundedTick + Starcaller.STAR_GROUNDED_TICKS) {
                    star.groundedTick = -1;
                }
            }
        }));
        StarcallerNetworking.init();
        LOGGER.info("[Starcaller] Initialized.");
    }

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    public static void groundStar(PlayerEntity cause, ServerWorld world, Star star) {
        updateStarGrounded(cause, world, star, world.getTime());
    }

    public static void freeStar(PlayerEntity cause, ServerWorld world, Star star) {
        updateStarGrounded(cause, world, star, -1);
    }

    private static void updateStarGrounded(PlayerEntity cause, ServerWorld world, Star star, long time) {
        if (star.groundedTick != time) {
            star.groundedTick = time;
            world.getPersistentStateManager().get(StarState.getPersistentStateType(), STATE_KEY).markDirty();
            StarcallerNetworking.syncStarGrounded(cause, world, star);
        }
    }

    public static void colorStar(PlayerEntity cause, ServerWorld world, Star star, int color) {
        if (star.color != color) {
            star.color = color;
            StarcallerNetworking.syncStarColor(cause, world, star);
        }
        TextColor nameColor = cause.getDisplayName().getStyle().getColor();
        star.editor = cause.getDisplayName().getString();
        star.editorColor = nameColor != null ? nameColor.getRgb() : 0xFFFFFF;
        world.getPersistentStateManager().get(StarState.getPersistentStateType(), STATE_KEY).markDirty();
    }
}
