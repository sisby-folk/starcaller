package folk.sisby.starcaller.client;

import folk.sisby.starcaller.Starcaller;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class StarcallerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Starcaller.ID + "_client");

    public static Random random = new Random();

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Starcaller.JAVELIN_ENTITY, JavelinEntityRenderer::new);
        ModelPredicateProviderRegistry.register(
                Starcaller.JAVELIN,
                new Identifier("throwing"),
                (stack, world, entity, i) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F
        );
        ColorProviderRegistry.ITEM.register((stack, index) -> (index > 0) ? (0x888888 +  random.nextInt(127)) : -1, Starcaller.JAVELIN);
        LOGGER.info("[Starcaller Client] Initialized.");
    }
}
