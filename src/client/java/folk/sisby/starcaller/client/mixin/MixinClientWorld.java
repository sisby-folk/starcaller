package folk.sisby.starcaller.client.mixin;

import folk.sisby.starcaller.Starcaller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
    @Inject(method = "method_23787", at = @At("HEAD"), cancellable = true)
    public void method_23787(float f, CallbackInfoReturnable<Float> cir) {
        if (MinecraftClient.getInstance().player != null && (MinecraftClient.getInstance().player.getMainHandStack().isOf(Starcaller.SPEAR) || MinecraftClient.getInstance().player.getOffHandStack().isOf(Starcaller.SPEAR))) {
            cir.setReturnValue(1.0F);
            cir.cancel();
        }
    }
}
