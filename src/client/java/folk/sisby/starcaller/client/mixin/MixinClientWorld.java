package folk.sisby.starcaller.client.mixin;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
    @Overwrite
    public float method_23787(float f) {
        return 1.0F;
    }
}
