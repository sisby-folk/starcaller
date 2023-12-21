package folk.sisby.starcaller.mixin;

import folk.sisby.starcaller.Starcaller;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {
    @Shadow public abstract ItemStack getStack();

    @Inject(method = "tick", at = @At("HEAD"))
    public void stardustTick(CallbackInfo ci) {
        if (getStack().isOf(Starcaller.STARDUST)) { // Replace me with sandman
            getStack().decrement(getStack().getCount());
        }
    }
}
