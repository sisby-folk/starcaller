package folk.sisby.starcaller.client.mixin;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Inject(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;end()Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;"))
    public void renderDebugStars(BufferBuilder bufferBuilder, CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir) {
        // +Z
        bufferBuilder.vertex(100.0F, 0 - 2, 0 - 2).next();
        bufferBuilder.vertex(100.0F, 0 - 2, 0 + 2).next();
        bufferBuilder.vertex(100.0F, 0 + 2, 0 + 2).next();
        bufferBuilder.vertex(100.0F, 0 + 2, 0 - 2).next();

        // -Z
        bufferBuilder.vertex(-100.0F, 0 - 2, 0).next();
        bufferBuilder.vertex(-100.0F, 0, 0 - 2).next();
        bufferBuilder.vertex(-100.0F, 0 + 2, 0).next();
        bufferBuilder.vertex(-100.0F, 0, 0 + 2).next();

        // Y
        bufferBuilder.vertex(0 - 8, 100F, 0 - 8).next();
        bufferBuilder.vertex(0 + 8, 100F, 0 - 8).next();
        bufferBuilder.vertex(0 + 8, 100F, 0 + 8).next();
        bufferBuilder.vertex(0 - 8, 100F, 0 + 8).next();

        // -Y
        bufferBuilder.vertex(0 - 8, -100F, 0).next();
        bufferBuilder.vertex(0, -100F, 0 + 8).next();
        bufferBuilder.vertex(0 + 8, -100F, 0).next();
        bufferBuilder.vertex(0, -100F, 0 - 8).next();

        // X
        bufferBuilder.vertex(0 - 2, 0 - 2, 100.0F).next();
        bufferBuilder.vertex(0 - 2, 0 + 2, 100.0F).next();
        bufferBuilder.vertex(0 + 2, 0 + 2, 100.0F).next();
        bufferBuilder.vertex(0 + 2, 0 - 2, 100.0F).next();

        // -X
        bufferBuilder.vertex(0 - 2, 0, -100.0F).next();
        bufferBuilder.vertex(0, 0 - 2, -100.0F).next();
        bufferBuilder.vertex(0 + 2, 0, -100.0F).next();
        bufferBuilder.vertex(0, 0 + 2, -100.0F).next();

        // +ZX
        bufferBuilder.vertex(70.0F, 70.0F - 2, 0 - 2).next();
        bufferBuilder.vertex(70.0F, 70.0F - 2, 0 + 2).next();
        bufferBuilder.vertex(70.0F, 70.0F + 2, 0 + 2).next();
        bufferBuilder.vertex(70.0F, 70.0F + 2, 0 - 2).next();

        // -ZX
        bufferBuilder.vertex(-70.0F, -70.0F - 2, 0).next();
        bufferBuilder.vertex(-70.0F, -70.0F, 0 - 2).next();
        bufferBuilder.vertex(-70.0F, -70.0F + 2, 0).next();
        bufferBuilder.vertex(-70.0F, -70.0F, 0 + 2).next();
    }
}
