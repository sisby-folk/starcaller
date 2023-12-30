package folk.sisby.starcaller.client;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.duck.StarcallerWorld;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class StarcallerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Starcaller.ID + "_client");
    public static final Identifier CROSSHAIR_TEXTURE = Starcaller.id("hud/crosshair");

    public static Random random = new Random();

    @Override
    public void onInitializeClient() {
        ModelPredicateProviderRegistry.register(Starcaller.SPEAR, new Identifier("throwing"),
                (stack, world, entity, i) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
        ColorProviderRegistry.ITEM.register((stack, index) -> (index > 0) ? (0x888888 + random.nextInt(127)) : -1, Starcaller.SPEAR);
        ClientTickEvents.END_WORLD_TICK.register((StarcallerClient::clientTick));
        StarcallerClientNetworking.init();
        LOGGER.info("[Starcaller Client] Initialized.");
    }

    public static void groundStar(ClientWorld world, Star star) {
        star.groundedTick = world.getTime();
        reloadStars(world);
    }

    public static void freeStar(ClientWorld world, Star star) {
        star.groundedTick = -1;
        reloadStars(world);
    }

    public static void colorStar(PlayerEntity cause, ClientWorld world, Star star, int color) {
        star.color = color;
        TextColor nameColor = cause.getDisplayName().getStyle().getColor();
        star.editor = cause.getDisplayName().getString();
        star.editorColor = nameColor != null ? nameColor.getRgb() : 0xFFFFFF;
        reloadStars(world);
    }

    public static void reloadStars(ClientWorld world) {
        MinecraftClient.getInstance().execute(() -> {
            if (world.worldRenderer.starsBuffer != null) {
                world.worldRenderer.renderStars();
            }
        });
    }

    private static void clientTick(ClientWorld world) {
        if (world instanceof StarcallerWorld scw) {
            List<Star> stars = scw.starcaller$getStars();
            boolean reloadStars = false;
            for (Star star : stars) {
                if (star.groundedTick + Starcaller.STAR_GROUNDED_TICKS <= world.getTime()) {
                    star.groundedTick = -1;
                    reloadStars = true;
                }
            }
            if (reloadStars) reloadStars(world);
        }
    }
}
