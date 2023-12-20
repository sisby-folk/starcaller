package folk.sisby.starcaller.client.mixin;

import folk.sisby.starcaller.Starcaller;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Unique private boolean hasDrawn = false;

    @Inject(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At("HEAD"))
    public void resetStarDebug(BufferBuilder bufferBuilder, CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir) {
        hasDrawn = false;
    }


    @Inject(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;vertex(DDD)Lnet/minecraft/client/render/VertexConsumer;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void setHasDrawn(BufferBuilder bufferBuilder, CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir, Random random, int i, double d, double e, double f, double g, double h, double j, double k, double l, double m, double n, double o, double p, double q, double r, double s, double t, double u, int v, double w, double x, double y, double z, double aa, double ab, double ac, double ad, double ae, double af, double ag, double ah) {
        if (Starcaller.DEBUG_SKY) {
            Starcaller.LOGGER.info("Gen Star {} {} {}", j, k, l);
        }
        hasDrawn = true;
    }


    @Inject(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextDouble()D"))
    public void renderDebugStars(BufferBuilder bufferBuilder, CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir) {
        if (Starcaller.DEBUG_SKY && hasDrawn) {
            // D, E, F -> Z, Y, -X
            // X, Y, Z -> -F, E, D

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
            bufferBuilder.vertex(0 - 2, 0 - 2, -100.0F).next();
            bufferBuilder.vertex(0 - 2, 0 + 2, -100.0F).next();
            bufferBuilder.vertex(0 + 2, 0 + 2, -100.0F).next();
            bufferBuilder.vertex(0 + 2, 0 - 2, -100.0F).next();

            // -X
            bufferBuilder.vertex(0 - 2, 0, 100.0F).next();
            bufferBuilder.vertex(0, 0 - 2, 100.0F).next();
            bufferBuilder.vertex(0 + 2, 0, 100.0F).next();
            bufferBuilder.vertex(0, 0 + 2, 100.0F).next();

            // +ZX
            bufferBuilder.vertex(70.0F, 0 - 2, -70.0F - 2).next();
            bufferBuilder.vertex(70.0F, 0 - 2, -70.0F + 2).next();
            bufferBuilder.vertex(70.0F, 0 + 2, -70.0F + 2).next();
            bufferBuilder.vertex(70.0F, 0 + 2, -70.0F - 2).next();

            // -ZX
            bufferBuilder.vertex(-70.0F, 0 - 2, 70.0F).next();
            bufferBuilder.vertex(-70.0F, 0, 70.0F - 2).next();
            bufferBuilder.vertex(-70.0F, 0 + 2, 70.0F).next();
            bufferBuilder.vertex(-70.0F, 0, 70.0F + 2).next();

            // -98.62, -7.03, 14.98
            // -ZX
            bufferBuilder.vertex(14.98, -7.03 - 2, 98.62F).next();
            bufferBuilder.vertex(14.98, -7.03, 98.62F + 2).next();
            bufferBuilder.vertex(14.98, -7.03 + 2, 98.62F).next();
            bufferBuilder.vertex(14.98, -7.03, 98.62F - 2).next();
        }
    }
}
