package folk.sisby.starcaller.client.mixin;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.client.StarcallerClient;
import folk.sisby.starcaller.duck.StarcallerWorld;
import folk.sisby.starcaller.util.StarUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
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
public abstract class MixinClientWorld implements StarcallerWorld {
    @Unique private long starcaller$seed = Starcaller.STAR_SEED;
    @Unique private int starcaller$iterations = Starcaller.STAR_ITERATIONS;
    @Unique private List<Star> starcaller$stars;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void generateStars(ClientPlayNetworkHandler clientPlayNetworkHandler, ClientWorld.Properties properties, RegistryKey<World> registryKey, RegistryEntry<World> registryEntry, int i, int j, Supplier<Profiler> supplier, WorldRenderer worldRenderer, boolean bl, long l, CallbackInfo ci) {
        starcaller$stars = StarUtil.generateStars(starcaller$seed, starcaller$iterations);
    }

    @Inject(method = "method_23787", at = @At("HEAD"), cancellable = true)
    public void fullBrightStarsWithSpear(float f, CallbackInfoReturnable<Float> cir) {
        if (MinecraftClient.getInstance().player != null && (MinecraftClient.getInstance().player.getMainHandStack().isOf(Starcaller.SPEAR) || MinecraftClient.getInstance().player.getOffHandStack().isOf(Starcaller.SPEAR))) {
            cir.setReturnValue(1.0F);
            cir.cancel();
        }
    }

    @Override
    public void starcaller$groundStar(PlayerEntity cause, Star star) {
        StarcallerClient.groundStar(((ClientWorld) (Object) this), star);
    }


    @Override
    public void starcaller$freeStar(PlayerEntity cause, Star star) {
        StarcallerClient.freeStar(((ClientWorld) (Object) this), star);
    }

    @Override
    public void starcaller$colorStar(PlayerEntity cause, Star star, int color) {
        StarcallerClient.colorStar(cause, ((ClientWorld) (Object) this), star, color);
    }

    @Override
    public long starcaller$getSeed() {
        return starcaller$seed;
    }

    @Override
    public int starcaller$getIterations() {
        return starcaller$iterations;
    }

    @Override
    public List<Star> starcaller$getStars() {
        return starcaller$stars;
    }

    @Override
    public void starcaller$setSeed(long seed) {
        this.starcaller$seed = seed;
    }

    @Override
    public void starcaller$setIterations(int iterations) {
        this.starcaller$iterations = iterations;
    }
}
