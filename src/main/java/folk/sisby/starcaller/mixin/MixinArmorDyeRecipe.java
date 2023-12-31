package folk.sisby.starcaller.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import folk.sisby.starcaller.item.StardustItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ArmorDyeRecipe;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(ArmorDyeRecipe.class)
public class MixinArmorDyeRecipe {
    @ModifyReturnValue(method = "matches(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/world/World;)Z", at = @At("RETURN"))
    public boolean dontMatchBadStardust(boolean original, RecipeInputInventory inventory, World world) {
        if (original) {
            for (int i = 0; i < inventory.size(); ++i) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof StardustItem) {
                        Long remainingTicks = StardustItem.getRemainingTicks(stack, world);
                        return remainingTicks != null && remainingTicks > 0 && (world.isClient || Objects.equals(remainingTicks, StardustItem.getWorldRemainingTicks(stack, world)));
                    }
                }
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "matches(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/world/World;)Z", at = @At("RETURN"))
    public boolean dontMatchDarkDyes(boolean original, RecipeInputInventory inventory, World world) {
        if (original) {
            boolean hasStardust = false;
            boolean hasDarkDye = false;
            for (int i = 0; i < inventory.size(); ++i) {
                ItemStack stack = inventory.getStack(i);
                if (!stack.isEmpty()) {
                    if (stack.getItem() instanceof StardustItem) {
                        hasStardust = true;
                    } else if (stack.getItem() instanceof DyeItem di && (
                            di.getColor() == DyeColor.BLACK ||
                            di.getColor() == DyeColor.BROWN ||
                            di.getColor() == DyeColor.GRAY
                    )) {
                        hasDarkDye = true;
                    }
                }
            }
            return !(hasStardust && hasDarkDye);
        }
        return original;
    }
}
