package folk.sisby.starcaller.item;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.duck.StarcallerWorld;
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
    public static final String KEY_STAR_DISPLAY = "display";
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
        NbtCompound display = new NbtCompound();
        display.putInt(KEY_STAR_COLOR, star.color);
        nbt.put(KEY_STAR_DISPLAY, display);
        if (star.editor != null) nbt.putString(KEY_EDITOR, star.editor);
        nbt.putInt(KEY_EDITOR_COLOR, star.editorColor);
        return stack;
    }

    @Override
    public Text getName(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(KEY_STAR_INDEX)) {
            return Text.translatable("item.starcaller.stardust.named", Text.translatable("star.starcaller.overworld.%s".formatted(nbt.getInt(KEY_STAR_INDEX)))
                    .setStyle(Style.EMPTY.withFormatting(Formatting.ITALIC).withColor(nbt.getCompound(KEY_STAR_DISPLAY).getInt(KEY_STAR_COLOR)))
            ).formatted(Formatting.GRAY);
        }
        return super.getName(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
        NbtCompound nbt = stack.getNbt();
        super.appendTooltip(stack, world, list, tooltipContext);
        if (nbt != null) {
            if (nbt.contains(KEY_EDITOR) && nbt.contains(KEY_EDITOR_COLOR)) {
                list.add(Text.translatable("item.starcaller.stardust.editor", Text.literal(nbt.getString(KEY_EDITOR)).setStyle(Style.EMPTY.withColor(nbt.getInt(KEY_EDITOR_COLOR)))).formatted(Formatting.GRAY));
            }
            if (nbt.contains(KEY_REMAINING_TICKS)) {
                list.add(Text.translatable("item.starcaller.stardust.countdown", Text.literal(String.valueOf(nbt.getLong(KEY_REMAINING_TICKS) / 20)).formatted(Formatting.GOLD)).formatted(Formatting.DARK_GRAY));
            }
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.contains(KEY_REMAINING_TICKS);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(KEY_REMAINING_TICKS)) {
            return (int) (nbt.getLong(KEY_REMAINING_TICKS) * 13.0F / Starcaller.STAR_GROUNDED_TICKS);
        }
        return super.getItemBarStep(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(KEY_STAR_INDEX)) {
            return nbt.getCompound(KEY_STAR_DISPLAY).getInt(KEY_STAR_COLOR);
        }
        return super.getItemBarColor(stack);
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity playerEntity) {
        super.onCraftByPlayer(stack, world, playerEntity);
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(KEY_STAR_INDEX) && world instanceof StarcallerWorld scw) {
            Star star = scw.starcaller$getStars().get(nbt.getInt(KEY_STAR_INDEX));
            scw.starcaller$colorStar(playerEntity, star, 0xFF000000 | nbt.getCompound(KEY_STAR_DISPLAY).getInt(KEY_STAR_COLOR));
            nbt.putString(KEY_EDITOR, star.editor);
            nbt.putInt(KEY_EDITOR_COLOR, star.editorColor);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int i, boolean bl) {
        super.inventoryTick(stack, world, entity, i, bl);
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(KEY_STAR_GROUNDED_TICK)) {
            long groundedTicks = world.getTime() - nbt.getLong(KEY_STAR_GROUNDED_TICK);
            if (groundedTicks > Starcaller.STAR_GROUNDED_TICKS) {
                stack.decrement(stack.getCount());
                return;
            }
            nbt.putLong(KEY_REMAINING_TICKS, Starcaller.STAR_GROUNDED_TICKS - groundedTicks);
        } else {
            stack.decrement(stack.getCount());
        }
    }
}
