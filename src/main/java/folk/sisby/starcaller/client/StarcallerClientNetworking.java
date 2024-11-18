package folk.sisby.starcaller.client;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.duck.StarcallerWorld;
import folk.sisby.starcaller.packet.S2CInitialStarState;
import folk.sisby.starcaller.packet.S2CUpdateColors;
import folk.sisby.starcaller.packet.S2CUpdateGrounded;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.List;

public class StarcallerClientNetworking {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(S2CInitialStarState.ID, StarcallerClientNetworking::setInitialStarState);
        ClientPlayNetworking.registerGlobalReceiver(S2CUpdateGrounded.ID, StarcallerClientNetworking::updateGrounded);
        ClientPlayNetworking.registerGlobalReceiver(S2CUpdateColors.ID, StarcallerClientNetworking::updateColors);
    }

    private static void setInitialStarState(S2CInitialStarState packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if (context.client().world instanceof StarcallerWorld scw) {
                scw.starcaller$setGeneratorValues(packet.seed(), packet.iterations());
                updateGrounded(new S2CUpdateGrounded(packet.groundedStars()), context);
                updateColors(new S2CUpdateColors(packet.starColors()), context);
            }
        });
    }

    private static void updateGrounded(S2CUpdateGrounded packet, ClientPlayNetworking.Context context) {
	    context.client().execute(() -> {
		    if (context.client().world instanceof StarcallerWorld scw) {
			    List<Star> stars = scw.starcaller$getStars();
			    packet.groundedStars().forEach((index, groundedTick) -> {
				    if (index < stars.size()) {
					    stars.get(index).groundedTick = groundedTick;
				    }
			    });
			    StarcallerClient.reloadStars(context.client().world);
		    }
	    });
    }

    private static void updateColors(S2CUpdateColors packet, ClientPlayNetworking.Context context) {
	    context.client().execute(() -> {
		    if (context.client().world instanceof StarcallerWorld scw) {
			    List<Star> stars = scw.starcaller$getStars();
			    packet.starColors().forEach((index, color) -> {
				    if (index < stars.size()) {
					    stars.get(index).color = color;
				    }
			    });
			    StarcallerClient.reloadStars(context.client().world);
		    }
	    });
    }
}
