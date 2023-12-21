package folk.sisby.starcaller.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.client.duck.StarcallerClientWorld;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Supplier;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow private @Nullable ClientWorld world;
    @Unique private int starIndex = -1;

    @Inject(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At("HEAD"))
    public void resetStarDebug(BufferBuilder bufferBuilder, CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir) {
        starIndex = -1;
    }

    @Inject(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextDouble()D"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void setHasDrawn(BufferBuilder bufferBuilder, CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir, Random random, int i, double d, double e, double f, double g, double h, double j, double k, double l, double m, double n, double o, double p, double q, double r) {
        if (Starcaller.DEBUG_SKY) {
            Starcaller.LOGGER.info("Gen Star {} {} {}", j, k, l);
        }
        starIndex++;
    }

    @ModifyReceiver(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;next()V"))
    public VertexConsumer setColorPerStar(VertexConsumer instance, BufferBuilder builder) {
        int color = Star.DEFAULT_COLOR;
        if (world instanceof StarcallerClientWorld scw) {
            List<Star> stars = scw.starcaller$getStars();
            if (starIndex < stars.size()) {
                Star star = stars.get(starIndex);
                boolean grounded = star.groundedTick != 0 && world.getTime() - star.groundedTick < Starcaller.STAR_GROUNDED_TICKS;
                if (grounded) {
                    color = 0x00FFFF00;
                } else {
                    color = star.color;
                }
            }
        }
        return instance.color(color);
    }

    @ModifyArg(method = "renderStars()V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"), index = 0)
    public Supplier<ShaderProgram> useColorSupplier(Supplier<ShaderProgram> supplier) {
        return GameRenderer::getPositionColorProgram;
    }

    @ModifyArg(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;begin(Lnet/minecraft/client/render/VertexFormat$DrawMode;Lnet/minecraft/client/render/VertexFormat;)V"), index = 1)
    public VertexFormat useColorBuffer(VertexFormat vertexFormat) {
        return VertexFormats.POSITION_COLOR;
    }

    @ModifyArg(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/VertexBuffer;draw(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/gl/ShaderProgram;)V", ordinal = 1), index = 2)
    public ShaderProgram useColorProgram(ShaderProgram shaderProgram) {
        return GameRenderer.getPositionColorProgram();
    }

    @Inject(method = "renderStars(Lnet/minecraft/client/render/BufferBuilder;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;end()Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;"))
    public void renderDebugStars(BufferBuilder bufferBuilder, CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir) {
        if (Starcaller.DEBUG_SKY) {
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
        }
    }
}
