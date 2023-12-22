package folk.sisby.starcaller;

import folk.sisby.starcaller.duck.StarcallerWorld;
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
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TextColor;
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
    public static final int STAR_GROUNDED_TICKS = 1200;
    public static final String STATE_KEY = "starcaller_stars";

    public static final Identifier S2C_UPDATE_GROUNDED = new Identifier(ID, "update_grounded");
    public static final Identifier S2C_UPDATE_COLORS = new Identifier(ID, "update_colors");

    public static final StardustItem STARDUST = Registry.register(Registries.ITEM, new Identifier(ID, "stardust"), new StardustItem(new FabricItemSettings().maxCount(1)));
    public static final SpearItem SPEAR = Registry.register(Registries.ITEM, new Identifier(ID, "spear"), new SpearItem(new FabricItemSettings().maxCount(1)));

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
        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> syncInitialStars(handler, sender)));
        LOGGER.info("[Starcaller] Initialized.");
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
            Map<Integer, Long> updateMap = new Int2ObjectArrayMap<>();
            int starIndex = ((StarcallerWorld) world).starcaller$getStars().indexOf(star);
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
    }

    public static void colorStar(PlayerEntity cause, ServerWorld world, Star star, int color) {
        if (star.color != color) {
            star.color = color;
            Map<Integer, Integer> updateMap = new Int2ObjectArrayMap<>();
            int starIndex = ((StarcallerWorld) world).starcaller$getStars().indexOf(star);
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
        TextColor nameColor = cause.getDisplayName().getStyle().getColor();
        star.editor = cause.getDisplayName().getString();
        star.editorColor = nameColor != null ? nameColor.getRgb() : 0xFFFFFF;
    }

    public static void syncInitialStars(ServerPlayNetworkHandler handler, PacketSender sender) {
        ServerWorld world = handler .getPlayer().getServerWorld();
        Map<Integer, Long> groundedMap = new Int2ObjectArrayMap<>();
        for (Star star : ((StarcallerWorld) world).starcaller$getStars()) {
            if (star.groundedTick != Star.DEFAULT_GROUNDED_TICK) groundedMap.put(((StarcallerWorld) world).starcaller$getStars().indexOf(star), star.groundedTick);
        }
        PacketByteBuf groundedBuf = PacketByteBufs.create();
        groundedBuf.writeMap(
                groundedMap,
                PacketByteBuf::writeInt,
                PacketByteBuf::writeLong
        );
        sender.sendPacket(S2C_UPDATE_GROUNDED, groundedBuf);
        Map<Integer, Integer> colorMap = new Int2ObjectArrayMap<>();
        for (Star star : ((StarcallerWorld) world).starcaller$getStars()) {
            if (star.color != Star.DEFAULT_COLOR) colorMap.put(((StarcallerWorld) world).starcaller$getStars().indexOf(star), star.color);
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
