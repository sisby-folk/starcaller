package folk.sisby.starcaller.client.mixin;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.client.duck.StarcallerClientWorld;
import folk.sisby.starcaller.util.StarUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public class MixinClientWorld implements StarcallerClientWorld {
    @Unique private List<Star> starCaller$stars;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void generateStars(ClientPlayNetworkHandler clientPlayNetworkHandler, ClientWorld.Properties properties, RegistryKey<World> registryKey, RegistryEntry<World> registryEntry, int i, int j, Supplier<Profiler> supplier, WorldRenderer worldRenderer, boolean bl, long l, CallbackInfo ci) {
        starCaller$stars = StarUtil.generateStars(Starcaller.STAR_SEED);
    }

    @Inject(method = "method_23787", at = @At("HEAD"), cancellable = true)
    public void fullBrightStarsWithSpear(float f, CallbackInfoReturnable<Float> cir) {
        if (MinecraftClient.getInstance().player != null && (MinecraftClient.getInstance().player.getMainHandStack().isOf(Starcaller.SPEAR) || MinecraftClient.getInstance().player.getOffHandStack().isOf(Starcaller.SPEAR))) {
            cir.setReturnValue(1.0F);
            cir.cancel();
        }
    }

    @Override
    public List<Star> starcaller$getStars() {
        return starCaller$stars;
    }
}
