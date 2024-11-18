package folk.sisby.starcaller.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.duck.StarcallerWorld;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @Shadow private @Nullable ClientWorld world;
    @Unique private int starIndex = -1;

    @Inject(method = "buildStarsBuffer", at = @At("HEAD"))
    public void resetStarDebug(Tessellator tessellator, CallbackInfoReturnable<BuiltBuffer> cir) {
        starIndex = -1;
    }

    @Inject(method = "buildStarsBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextDouble()D"))
    public void countSuccessfulStars(Tessellator tessellator, CallbackInfoReturnable<BuiltBuffer> cir) {
        starIndex++;
    }

    @ModifyArg(method = "buildStarsBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;create(J)Lnet/minecraft/util/math/random/Random;"))
    public long useCustomSeed(long original) {
        if (world instanceof StarcallerWorld scw) {
            return scw.starcaller$getSeed();
        }
        return original;
    }

    @ModifyConstant(method = "buildStarsBuffer", constant = @Constant(intValue = 1500))
    public int useCustomLimit(int constant) {
        if (world instanceof StarcallerWorld scw) {
            return scw.starcaller$getIterations();
        }
        return constant;
    }

    @ModifyExpressionValue(method = "buildStarsBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilder;vertex(Lorg/joml/Vector3f;)Lnet/minecraft/client/render/VertexConsumer;"))
    public VertexConsumer setColorPerStar(VertexConsumer instance) {
        int color = Star.DEFAULT_COLOR;
        if (world instanceof StarcallerWorld scw) {
            List<Star> stars = scw.starcaller$getStars();
            if (starIndex < stars.size()) {
                Star star = stars.get(starIndex);
                boolean grounded = star.groundedTick != 0 && world.getTime() - star.groundedTick < Starcaller.CONFIG.starGroundedTicks;
                if (grounded) {
                    color = 0x00FFFF00;
                } else {
                    color = star.color;
                }
            }
        }
        return instance.color(color);
    }

    @ModifyArg(method = "buildStarsBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Tessellator;begin(Lnet/minecraft/client/render/VertexFormat$DrawMode;Lnet/minecraft/client/render/VertexFormat;)Lnet/minecraft/client/render/BufferBuilder;"), index = 1)
    public VertexFormat useColorBuffer(VertexFormat vertexFormat) {
        return VertexFormats.POSITION_COLOR;
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/VertexBuffer;draw(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/gl/ShaderProgram;)V", ordinal = 1), index = 2)
    public ShaderProgram useColorProgram(ShaderProgram shaderProgram) {
        return GameRenderer.getPositionColorProgram();
    }
}
