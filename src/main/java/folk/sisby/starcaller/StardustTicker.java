package folk.sisby.starcaller;

import net.minecraft.item.ItemStack;

public interface StardustTicker {
    default boolean isItemBarVisible(ItemStack stack) { return false; }
    default int getItemBarStep(ItemStack stack) { return 0; }
}
