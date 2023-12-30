package folk.sisby.starcaller.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import folk.sisby.starcaller.Starcaller;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @WrapWithCondition(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 3))
    private boolean hideStardustDyed(List<Text> instance, Object tooltip) {
        return !((ItemStack) (Object) this).isOf(Starcaller.STARDUST);
    }
}
