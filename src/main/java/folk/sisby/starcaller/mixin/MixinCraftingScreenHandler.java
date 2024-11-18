package folk.sisby.starcaller.mixin;

import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.item.StardustItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(CraftingScreenHandler.class)
public class MixinCraftingScreenHandler {
    @ModifyArgs(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"))
    private static void applyStardustEditor(Args args, ScreenHandler handler, World world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory, @Nullable RecipeEntry<CraftingRecipe> recipe) {
		ItemStack stack = args.get(1);
        if (stack.contains(Starcaller.STAR) && stack.getItem() instanceof StardustItem) {
            Text name = player.getDisplayName();
            if (name != null) {
                TextColor nameColor = name.getStyle().getColor();
				stack.apply(Starcaller.STAR, null, s -> s.withEditor(player.getDisplayName().getString(), nameColor != null ? nameColor.getRgb() : 0xFFFFFF));
            }
        }
    }
}
