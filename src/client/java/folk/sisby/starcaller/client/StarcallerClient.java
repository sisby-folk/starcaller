package folk.sisby.starcaller.client;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.client.duck.StarcallerClientWorld;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class StarcallerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Starcaller.ID + "_client");

    public static Random random = new Random();

    @Override
    public void onInitializeClient() {
        ModelPredicateProviderRegistry.register(
                Starcaller.SPEAR,
                new Identifier("throwing"),
                (stack, world, entity, i) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
        ColorProviderRegistry.ITEM.register((stack, index) -> (index > 0) ? (0x888888 + random.nextInt(127)) : -1, Starcaller.SPEAR);
        ClientPlayNetworking.registerGlobalReceiver(Starcaller.S2C_UPDATE_GROUNDED, ((client, handler, buf, responseSender) -> {
            Map<Integer, Long> map = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readLong);
            if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getWorld() instanceof StarcallerClientWorld scw) {
                List<Star> stars = scw.starcaller$getStars();
                map.forEach((index, groundedTick) -> {
                    if (index < stars.size()) {
                        stars.get(index).groundedTick = groundedTick;
                    }
                });
                client.execute(() -> MinecraftClient.getInstance().player.clientWorld.worldRenderer.renderStars());
            }
        }));
        LOGGER.info("[Starcaller Client] Initialized.");
    }
}
