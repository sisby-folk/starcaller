package folk.sisby.starcaller;

import folk.sisby.starcaller.item.SpearItem;
import folk.sisby.starcaller.item.StardustItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Starcaller implements ModInitializer {
    public static final String ID = "starcaller";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);
    public static final boolean DEBUG_SKY = false;
    public static final long STAR_SEED = 10842L;
    public static final int STAR_GROUNDED_TICKS = 600;
    public static StarState STATE;

    public static final Identifier S2C_UPDATE_GROUNDED = new Identifier(ID, "update_grounded");
    public static final Identifier S2C_UPDATE_COLORS = new Identifier(ID, "update_colors");

    public static final StardustItem STARDUST = Registry.register(Registries.ITEM, new Identifier(ID, "stardust"), new StardustItem(new FabricItemSettings().maxCount(1)));
    public static final SpearItem SPEAR = Registry.register(Registries.ITEM, new Identifier(ID, "spear"), new SpearItem(new FabricItemSettings().maxCount(1)));

    @Override
    public void onInitialize() {
        ServerWorldEvents.LOAD.register(((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                STATE = world.getPersistentStateManager().getOrCreate(StarState.getPersistentStateType(), "starcaller_stars");
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
            for (Star star : STATE.stars) {
                if (world.getTime() > star.groundedTick + Starcaller.STAR_GROUNDED_TICKS) {
                    star.groundedTick = -1;
                }
            }
        }));
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> syncInitialStars(sender)));
        LOGGER.info("[Starcaller] Initialized.");
    }

    public static void groundStar(PlayerEntity cause, ServerWorld world, Star star) {
        star.groundedTick = world.getTime();
        Map<Integer, Long> updateMap = new Int2ObjectArrayMap<>();
        int starIndex = STATE.stars.indexOf(star);
        updateMap.put(starIndex, star.groundedTick);
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player != cause) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeMap(
                        updateMap,
                        PacketByteBuf::writeInt,
                        PacketByteBuf::writeLong
                );
                ServerPlayNetworking.send(player, S2C_UPDATE_GROUNDED, buf);
            }
        }
    }

    public static void colorStar(PlayerEntity cause, ServerWorld world, Star star, int color) {
        star.color = color;
        Map<Integer, Integer> updateMap = new Int2ObjectArrayMap<>();
        int starIndex = STATE.stars.indexOf(star);
        updateMap.put(starIndex, star.color);
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player != cause) {
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeMap(
                        updateMap,
                        PacketByteBuf::writeInt,
                        PacketByteBuf::writeInt
                );
                ServerPlayNetworking.send(player, S2C_UPDATE_COLORS, buf);
            }
        }
    }

    public static void syncInitialStars(PacketSender sender) {
        Map<Integer, Long> groundedMap = new Int2ObjectArrayMap<>();
        for (Star star : STATE.stars) {
            if (star.groundedTick != Star.DEFAULT_GROUNDED_TICK) groundedMap.put(STATE.stars.indexOf(star), star.groundedTick);
        }
        PacketByteBuf groundedBuf = PacketByteBufs.create();
        groundedBuf.writeMap(
                groundedMap,
                PacketByteBuf::writeInt,
                PacketByteBuf::writeLong
        );
        sender.sendPacket(S2C_UPDATE_GROUNDED, groundedBuf);
        Map<Integer, Integer> colorMap = new Int2ObjectArrayMap<>();
        for (Star star : STATE.stars) {
            if (star.color != Star.DEFAULT_COLOR) colorMap.put(STATE.stars.indexOf(star), star.color);
        }
        PacketByteBuf colorBuf = PacketByteBufs.create();
        colorBuf.writeMap(
                colorMap,
                PacketByteBuf::writeInt,
                PacketByteBuf::writeInt
        );
        sender.sendPacket(S2C_UPDATE_GROUNDED, colorBuf);
    }
}
