package folk.sisby.starcaller;

import folk.sisby.starcaller.entity.JavelinEntity;
import folk.sisby.starcaller.item.JavelinItem;
import folk.sisby.starcaller.item.StardustItem;
import folk.sisby.starcaller.util.StarUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starcaller implements ModInitializer {
    public static final String ID = "starcaller";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
    public static final boolean DEBUG_SKY = false;
    public StarcallerState STATE;

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
        ServerWorldEvents.LOAD.register(((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                STATE = world.getPersistentStateManager().getOrCreate(StarcallerState.getPersistentStateType(), "aStarcallerState");
                if (Starcaller.DEBUG_SKY) {
                    LOGGER.info("[Starcaller] Start Logging World Stars");
                    STATE.stars.forEach(s -> {
                        LOGGER.info("[Starcaller] {}", s);
                    });
                    LOGGER.info("[Starcaller] End Logging World Stars");
                }
            }
        }));
        ServerTickEvents.END_WORLD_TICK.register((world -> {
            for (ServerPlayerEntity player : world.getPlayers()) {
                for (ItemStack handItem : player.getHandItems()) {
                    if (handItem.isOf(JAVELIN)) {
                        Entity camera = player.getCameraEntity();
                        Vec3d cursorCoordinates = StarUtil.correctForSkyAngle(StarUtil.getStarCursor(camera.getYaw(), camera.getPitch()), world.getSkyAngle(1.0F));
                        for (int i = 0; i < STATE.stars.size(); i++) {
                            StarcallerStar star = STATE.stars.get(i);
                            if (cursorCoordinates.isInRange(new Vec3d(star.x, star.y, star.z), 3)) {
                                player.sendMessageToClient(Text.translatable("messages.starcaller.star.info", i, Text.translatable("star.starcaller.overworld.%s".formatted(i)), STATE.stars.get(i).toString()), true);
                                return;
                            }
                        }
                        player.sendMessageToClient(Text.literal("No Stars"), true);
                        return;
                    }
                }
            }
        }));
        LOGGER.info("[Starcaller] Initialized.");
    }
}
