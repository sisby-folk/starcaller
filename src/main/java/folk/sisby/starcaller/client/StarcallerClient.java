package folk.sisby.starcaller.client;

import folk.sisby.starcaller.Starcaller;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarcallerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Starcaller.ID + "_client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Starcaller Client] Initialized.");
    }
}
