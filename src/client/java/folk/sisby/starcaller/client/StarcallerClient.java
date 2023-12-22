package folk.sisby.starcaller.client;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.duck.StarcallerWorld;
import folk.sisby.starcaller.util.StarUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StarcallerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Starcaller.ID + "_client");
    public static final Identifier CROSSHAIR_TEXTURE = new Identifier(Starcaller.ID, "hud/crosshair");

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
            if (client.player != null && client.player.getWorld() instanceof StarcallerWorld scw) {
                List<Star> stars = scw.starcaller$getStars();
                map.forEach((index, groundedTick) -> {
                    if (index < stars.size()) {
                        stars.get(index).groundedTick = groundedTick;
                    }
                });
                client.execute(() -> {
                    if (client.player.clientWorld.worldRenderer.starsBuffer != null) {
                        client.player.clientWorld.worldRenderer.renderStars();
                    }
                });
            }
        }));
        ClientPlayNetworking.registerGlobalReceiver(Starcaller.S2C_UPDATE_COLORS, ((client, handler, buf, responseSender) -> {
            Map<Integer, Integer> map = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readInt);
            if (client.player != null && client.player.getWorld() instanceof StarcallerWorld scw) {
                List<Star> stars = scw.starcaller$getStars();
                map.forEach((index, color) -> {
                    if (index < stars.size()) {
                        stars.get(index).color = color;
                    }
                });
                client.execute(() -> {
                    if (client.player.clientWorld.worldRenderer.starsBuffer != null) {
                        client.player.clientWorld.worldRenderer.renderStars();
                    }
                });
            }
        }));
        ClientTickEvents.END_WORLD_TICK.register((world -> {
            if (world instanceof StarcallerWorld scw) {
                List<Star> stars = scw.starcaller$getStars();
                boolean reloadStars = false;
                for (Star star : stars) {
                    if (star.groundedTick + Starcaller.STAR_GROUNDED_TICKS <= world.getTime()) {
                        star.groundedTick = -1;
                        reloadStars = true;
                    }
                }
                if (reloadStars) {
                    MinecraftClient.getInstance().execute(() -> {
                        if (world.worldRenderer.starsBuffer != null) {
                            world.worldRenderer.renderStars();
                        }
                    });
                }
                ClientPlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    for (ItemStack handItem : player.getHandItems()) {
                        if (handItem.isOf(Starcaller.SPEAR)) {
                            if (player.raycast(12 * 16, 1.0F, false).getType() == HitResult.Type.MISS) {
                                Vec3d cursorCoordinates = StarUtil.correctForSkyAngle(StarUtil.getStarCursor(player.getHeadYaw(), player.getPitch()), world.getSkyAngle(1.0F));
                                Star closestStar = stars.stream().filter(s -> s.groundedTick == -1 || s.groundedTick + Starcaller.STAR_GROUNDED_TICKS < world.getTime()).filter(s -> s.groundedTick == -1 || s.groundedTick + Starcaller.STAR_GROUNDED_TICKS < world.getTime()).min(Comparator.comparingDouble(s -> s.pos.squaredDistanceTo(cursorCoordinates))).get();
                                if (cursorCoordinates.isInRange(closestStar.pos, 4)) {
                                    int i = stars.indexOf(closestStar);
                                    player.sendMessage(Text.translatable("messages.starcaller.star.info", Text.translatable("star.starcaller.overworld.%s".formatted(i)).setStyle(Style.EMPTY.withFormatting(Formatting.ITALIC).withColor(closestStar.color))), true);
                                    return;
                                }
                                player.sendMessage(Text.empty(), true);
                                return;
                            }
                        }
                    }
                }
            }
        }));
        LOGGER.info("[Starcaller Client] Initialized.");
    }

    public static void groundStar(PlayerEntity cause, ClientWorld world, Star star) {
        star.groundedTick = world.getTime();
    }

    public static void freeStar(PlayerEntity cause, ClientWorld clientWorld, Star star) {
        star.groundedTick = -1;
    }

    public static void colorStar(PlayerEntity cause, Star star, int color) {
        star.color = color;
        TextColor nameColor = cause.getDisplayName().getStyle().getColor();
        star.editor = cause.getDisplayName().getString();
        star.editorColor = nameColor != null ? nameColor.getRgb() : 0xFFFFFF;
    }
}
