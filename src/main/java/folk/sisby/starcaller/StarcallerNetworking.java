package folk.sisby.starcaller;

import folk.sisby.starcaller.duck.StarcallerWorld;
import folk.sisby.starcaller.packet.S2CInitialStarState;
import folk.sisby.starcaller.packet.S2CUpdateColors;
import folk.sisby.starcaller.packet.S2CUpdateGrounded;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.Map;

public class StarcallerNetworking {
    public static void init() {
	    PayloadTypeRegistry.playS2C().register(S2CInitialStarState.ID, S2CInitialStarState.CODEC);
	    PayloadTypeRegistry.playS2C().register(S2CUpdateGrounded.ID, S2CUpdateGrounded.CODEC);
	    PayloadTypeRegistry.playS2C().register(S2CUpdateColors.ID, S2CUpdateColors.CODEC);

	    ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> sendInitialStarState(handler.player)));
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
            ServerPlayNetworking.send(player, new S2CInitialStarState(
				((StarcallerWorld) world).starcaller$getSeed(),
	            ((StarcallerWorld) world).starcaller$getIterations(),
	            groundedMap,
	            colorMap
            ));
        }
    }

    public static void syncStarGrounded(PlayerEntity cause, ServerWorld world, Star star) {
        int starIndex = ((StarcallerWorld) world).starcaller$getStars().indexOf(star);
        Map<Integer, Long> groundedMap = Map.of(starIndex, star.groundedTick);
        S2CUpdateGrounded packet = new S2CUpdateGrounded(groundedMap);
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player == cause) continue;
            ServerPlayNetworking.send(player, packet);
        }
    }

    public static void syncStarColor(PlayerEntity cause, ServerWorld world, Star star) {
        int starIndex = ((StarcallerWorld) world).starcaller$getStars().indexOf(star);
        Map<Integer, Integer> colorMap = Map.of(starIndex, star.color);
	    S2CUpdateColors packet = new S2CUpdateColors(colorMap);
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player == cause) continue;
            ServerPlayNetworking.send(player, packet);
        }
    }
}
