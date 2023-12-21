package folk.sisby.starcaller;

import folk.sisby.starcaller.entity.SpearEntity;
import folk.sisby.starcaller.item.SpearItem;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class Starcaller implements ModInitializer {
    public static final String ID = "starcaller";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
    public static final boolean DEBUG_SKY = false;
    public StarcallerState STATE;

    public static final StardustItem STARDUST = Registry.register(Registries.ITEM, new Identifier(ID, "stardust"), new StardustItem(new FabricItemSettings().maxCount(1)));
    public static final SpearItem SPEAR = Registry.register(Registries.ITEM, new Identifier(ID, "spear"), new SpearItem(new FabricItemSettings().maxCount(1)));
    public static final EntityType<SpearEntity> SPEAR_ENTITY = Registry.register(Registries.ENTITY_TYPE, new Identifier(ID, "spear"),
            FabricEntityTypeBuilder.<SpearEntity>create(SpawnGroup.MISC, SpearEntity::new)
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
                    if (handItem.isOf(SPEAR)) {
                        Entity camera = player.getCameraEntity();
                        if (player.raycast(world.getServer().getPlayerManager().getViewDistance() * 16, 1.0F, false).getType() == HitResult.Type.MISS) {
                            Vec3d cursorCoordinates = StarUtil.correctForSkyAngle(StarUtil.getStarCursor(camera.getYaw(), camera.getPitch()), world.getSkyAngle(1.0F));
                            StarcallerStar closestStar = STATE.stars.stream().min(Comparator.comparingDouble(s -> s.pos.squaredDistanceTo(cursorCoordinates))).get();
                            if (cursorCoordinates.isInRange(closestStar.pos, 4)) {
                                int i = STATE.stars.indexOf(closestStar);
                                player.sendMessageToClient(Text.translatable("messages.starcaller.star.info" + (DEBUG_SKY ? ".debug" : ""), i, Text.translatable("star.starcaller.overworld.%s".formatted(i)).formatted(Formatting.ITALIC), STATE.stars.get(i).toString()), true);
                                return;
                            }
                            player.sendMessageToClient(Text.empty(), true);
                            return;
                        }
                    }
                }
            }
        }));
        LOGGER.info("[Starcaller] Initialized.");
    }
}
