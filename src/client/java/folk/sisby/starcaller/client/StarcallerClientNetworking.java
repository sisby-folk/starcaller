package folk.sisby.starcaller.client;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.StarcallerNetworking;
import folk.sisby.starcaller.duck.StarcallerWorld;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.List;
import java.util.Map;

public class StarcallerClientNetworking {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(StarcallerNetworking.S2C_INITIAL_STAR_STATE, StarcallerClientNetworking::setInitialStarState);
        ClientPlayNetworking.registerGlobalReceiver(StarcallerNetworking.S2C_UPDATE_GROUNDED, StarcallerClientNetworking::updateGrounded);
        ClientPlayNetworking.registerGlobalReceiver(StarcallerNetworking.S2C_UPDATE_COLORS, StarcallerClientNetworking::updateColors);
    }

    private static void setInitialStarState(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.player != null && client.player.clientWorld instanceof StarcallerWorld scw) {
            scw.starcaller$setSeed(buf.readLong());
            scw.starcaller$setIterations(buf.readInt());
            updateGrounded(client, handler, buf, responseSender);
            updateColors(client, handler, buf, responseSender);
        }
    }

    private static void updateGrounded(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Map<Integer, Long> groundedMap = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readLong);
        if (client.player != null && client.player.getWorld() instanceof StarcallerWorld scw) {
            List<Star> stars = scw.starcaller$getStars();
            groundedMap.forEach((index, groundedTick) -> {
                if (index < stars.size()) {
                    stars.get(index).groundedTick = groundedTick;
                }
            });
            StarcallerClient.reloadStars(client.player.clientWorld);
        }
    }

    private static void updateColors(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Map<Integer, Integer> colorMap = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readInt);
        if (client.player != null && client.player.getWorld() instanceof StarcallerWorld scw) {
            List<Star> stars = scw.starcaller$getStars();
            colorMap.forEach((index, color) -> {
                if (index < stars.size()) {
                    stars.get(index).color = color;
                }
            });
            StarcallerClient.reloadStars(client.player.clientWorld);
        }
    }
}
