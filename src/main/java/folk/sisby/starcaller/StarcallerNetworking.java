package folk.sisby.starcaller;

import folk.sisby.starcaller.duck.StarcallerWorld;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Map;

public class StarcallerNetworking {
    public static final Identifier S2C_INITIAL_STAR_STATE = Starcaller.id("initial_star_state");
    public static final Identifier S2C_UPDATE_GROUNDED = Starcaller.id("update_grounded");
    public static final Identifier S2C_UPDATE_COLORS = Starcaller.id("update_colors");

    public static void init() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> sendInitialStarState(player));
    }

    public static void sendInitialStarState(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        if (world.getRegistryKey() == World.OVERWORLD) {
            Map<Integer, Long> groundedMap = new Int2ObjectArrayMap<>();
            Map<Integer, Integer> colorMap = new Int2ObjectArrayMap<>();
            for (Star star : ((StarcallerWorld) world).starcaller$getStars()) {
                if (groundedMap.isEmpty() || star.groundedTick != Star.DEFAULT_GROUNDED_TICK) groundedMap.put(((StarcallerWorld) world).starcaller$getStars().indexOf(star), star.groundedTick);
                if (colorMap.isEmpty() || star.color != Star.DEFAULT_COLOR) colorMap.put(((StarcallerWorld) world).starcaller$getStars().indexOf(star), star.color);
            }
            PacketByteBuf stateBuf = PacketByteBufs.create();
            stateBuf.writeLong(((StarcallerWorld) world).starcaller$getSeed());
            stateBuf.writeInt(((StarcallerWorld) world).starcaller$getIterations());
            stateBuf.writeMap(groundedMap, PacketByteBuf::writeInt, PacketByteBuf::writeLong);
            stateBuf.writeMap(colorMap, PacketByteBuf::writeInt, PacketByteBuf::writeInt);
            ServerPlayNetworking.send(player, S2C_INITIAL_STAR_STATE, stateBuf);
        }
    }

    public static void syncStarGrounded(PlayerEntity cause, ServerWorld world, Star star) {
        int starIndex = ((StarcallerWorld) world).starcaller$getStars().indexOf(star);
        Map<Integer, Long> groundedMap = Map.of(starIndex, star.groundedTick);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeMap(groundedMap, PacketByteBuf::writeInt, PacketByteBuf::writeLong);
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player == cause) continue;
            ServerPlayNetworking.send(player, S2C_UPDATE_GROUNDED, buf);
        }
    }

    public static void syncStarColor(PlayerEntity cause, ServerWorld world, Star star) {
        int starIndex = ((StarcallerWorld) world).starcaller$getStars().indexOf(star);
        Map<Integer, Integer> colorMap = Map.of(starIndex, star.color);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeMap(colorMap, PacketByteBuf::writeInt, PacketByteBuf::writeInt);
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player == cause) continue;
            ServerPlayNetworking.send(player, S2C_UPDATE_COLORS, buf);
        }
    }
}
