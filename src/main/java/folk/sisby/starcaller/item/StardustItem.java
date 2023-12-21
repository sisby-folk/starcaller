package folk.sisby.starcaller.item;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StardustItem extends Item implements DyeableItem {
    public static final String KEY_STAR_INDEX = "star";
    public static final String KEY_STAR_GROUNDED_TICK = "groundedTick";
    public static final String KEY_STAR_COLOR = "color";
    public static final String KEY_EDITOR = "editor";
    public static final String KEY_EDITOR_COLOR = "editorColor";
    public static final String KEY_REMAINING_TICKS = "remainingTicks";

    public StardustItem(Settings settings) {
        super(settings);
    }

    public static ItemStack fromStar(int index, Star star) {
        ItemStack stack = Starcaller.STARDUST.getDefaultStack().copy();
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(KEY_STAR_INDEX, index);
        nbt.putLong(KEY_STAR_GROUNDED_TICK, star.groundedTick);
        nbt.putInt(KEY_STAR_COLOR, star.color);
        if (star.editor != null) nbt.putString(KEY_EDITOR, star.editor);
        nbt.putInt(KEY_EDITOR_COLOR, star.editorColor);
        return stack;
    }

    @Override
    public Text getName(ItemStack itemStack) {
        if (itemStack.hasNbt() && itemStack.getNbt().contains(KEY_STAR_INDEX)) {
            return Text.translatable("item.starcaller.stardust.named", Text.translatable("star.starcaller.overworld.%s".formatted(itemStack.getNbt().getInt(KEY_STAR_INDEX)))
                    .setStyle(Style.EMPTY.withFormatting(Formatting.ITALIC).withColor(itemStack.getNbt().getInt(KEY_STAR_COLOR)))
            );
        }
        return super.getName(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
        super.appendTooltip(itemStack, world, list, tooltipContext);
        if (itemStack.hasNbt() && itemStack.getNbt().contains(KEY_EDITOR) && itemStack.getNbt().contains(KEY_EDITOR_COLOR)) {
            list.add(Text.translatable("item.starcaller.stardust.editor", Text.literal(itemStack.getNbt().getString(KEY_EDITOR)).setStyle(Style.EMPTY.withColor(itemStack.getNbt().getInt(KEY_EDITOR_COLOR)))));
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack itemStack) {
        return itemStack.hasNbt() && itemStack.getNbt().contains(KEY_REMAINING_TICKS);
    }

    @Override
    public int getItemBarStep(ItemStack itemStack) {
        if (itemStack.hasNbt() && itemStack.getNbt().contains(KEY_REMAINING_TICKS)) {
            return (int) (itemStack.getNbt().getLong(KEY_REMAINING_TICKS) * 13.0F / Starcaller.STAR_GROUNDED_TICKS);
        }
        return super.getItemBarStep(itemStack);
    }

    @Override
    public int getItemBarColor(ItemStack itemStack) {
        if (itemStack.hasNbt() && itemStack.getNbt().contains(KEY_STAR_COLOR)) {
            return itemStack.getNbt().getInt(KEY_STAR_COLOR);
        }
        return super.getItemBarColor(itemStack);
    }

    @Override
    public void onCraftByPlayer(ItemStack itemStack, World world, PlayerEntity playerEntity) {
        super.onCraftByPlayer(itemStack, world, playerEntity);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int i, boolean bl) {
        super.inventoryTick(itemStack, world, entity, i, bl);
        if (itemStack.hasNbt() && itemStack.getNbt().contains(KEY_STAR_GROUNDED_TICK)) {
            long groundedTicks = world.getTime() - itemStack.getNbt().getLong(KEY_STAR_GROUNDED_TICK);
            if (groundedTicks > Starcaller.STAR_GROUNDED_TICKS) {
                if (entity instanceof PlayerEntity player) {
                    itemStack.decrement(itemStack.getCount());
                }
                return;
            }
            itemStack.getNbt().putLong(KEY_REMAINING_TICKS, Starcaller.STAR_GROUNDED_TICKS - groundedTicks);
        } else {
            itemStack.decrement(itemStack.getCount());
        }
    }
}
