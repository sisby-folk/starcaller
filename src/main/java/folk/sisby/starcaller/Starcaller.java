package folk.sisby.starcaller;

import folk.sisby.starcaller.entity.JavelinEntity;
import folk.sisby.starcaller.item.JavelinItem;
import folk.sisby.starcaller.item.StardustItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Starcaller implements ModInitializer {
    public static final String ID = "starcaller";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
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
                LOGGER.info("[Starcaller] Start Logging World Stars");
                STATE.stars.forEach(s -> {
                    LOGGER.info("[Starcaller] {}", s);
                });
                LOGGER.info("[Starcaller] End Logging World Stars");
            }
        }));
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            STATE.stars.forEach(s -> {
                handler.player.sendMessageToClient(Text.literal(s.toString()), false);
            });

        }));
        LOGGER.info("[Starcaller] Initialized.");
    }

}
