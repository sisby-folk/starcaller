package folk.sisby.starcaller.mixin;

import folk.sisby.starcaller.item.StardustItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CraftingScreenHandler.class)
public class MixinCraftingScreenHandler {
    @ModifyVariable(method = "updateResult", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/recipe/CraftingRecipe;craft(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/item/ItemStack;"), ordinal = 1)
    private static ItemStack applyStardustEditor(ItemStack stack, ScreenHandler handler, World world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && stack.getItem() instanceof StardustItem) {
            Text name = player.getDisplayName();
            if (name != null) {
                TextColor nameColor = name.getStyle().getColor();
                nbt.putString(StardustItem.KEY_EDITOR, player.getDisplayName().getString());
                nbt.putInt(StardustItem.KEY_EDITOR_COLOR, nameColor != null ? nameColor.getRgb() : 0xFFFFFF);
            }
        }
        return stack;
    }
}
