package folk.sisby.starcaller.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.item.StardustItem;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
    @WrapWithCondition(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V", ordinal = 4))
    private boolean hideStardustDyed(ItemStack instance, ComponentType<Object> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
        return !((ItemStack) (Object) this).isOf(Starcaller.STARDUST);
    }

	@ModifyArgs(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;appendTooltip(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/item/tooltip/TooltipType;)V"))
	private void appendStardustTooltip(Args args, Item.TooltipContext context, PlayerEntity player, TooltipType type) {
		if (((ItemStack) (Object) this).getItem() instanceof StardustItem && player != null) {
			Long remainingTicks = StardustItem.getRemainingTicks(((ItemStack) (Object) this), player.getWorld());
			if (remainingTicks != null) {
				if (remainingTicks < 0) {
					((List<Text>) args.get(2)).clear();
					return;
				}
				((List<Text>) args.get(2)).add(StardustItem.getCountdown(remainingTicks));
			}
		}
	}
}
