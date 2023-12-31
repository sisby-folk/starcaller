package folk.sisby.starcaller.client;

import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.StardustTicker;
import folk.sisby.starcaller.item.StardustItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.joml.Math;

import static folk.sisby.starcaller.item.StardustItem.KEY_STAR_GROUNDED_TICK;

public class StardustTickerImpl implements StardustTicker {
    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        Long remainingTicks = StardustItem.getRemainingTicks(stack, MinecraftClient.getInstance().world);
        return remainingTicks != null && remainingTicks > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(KEY_STAR_GROUNDED_TICK) && MinecraftClient.getInstance().world != null) {
            long remainingTicks = Starcaller.STAR_GROUNDED_TICKS + nbt.getLong(KEY_STAR_GROUNDED_TICK) - MinecraftClient.getInstance().world.getTime();
            return (int) Math.clamp(0, 13.0F, (remainingTicks * 13.0F / Starcaller.STAR_GROUNDED_TICKS));

        }
        return StardustTicker.super.getItemBarStep(stack);
    }
}
