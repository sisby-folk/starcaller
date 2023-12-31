package folk.sisby.starcaller.item;

import com.unascribed.lib39.sandman.api.TicksAlwaysItem;
import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.duck.StarcallerWorld;
import folk.sisby.starcaller.util.ColorUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Objects;

public class StardustItem extends Item implements DyeableItem, TicksAlwaysItem {
    public static final String KEY_STAR_INDEX = "star";
    public static final String KEY_STAR_GROUNDED_TICK = "groundedTick";
    public static final String KEY_STAR_DISPLAY = "display";
    public static final String KEY_STAR_COLOR = "color";
    public static final String KEY_EDITOR = "editor";
    public static final String KEY_EDITOR_COLOR = "editorColor";

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

    public static Long getRemainingTicks(ItemStack stack, World world) {
        NbtCompound nbt = stack.getNbt();
        return world != null && nbt != null && nbt.contains(KEY_STAR_GROUNDED_TICK) ? Starcaller.STAR_GROUNDED_TICKS + nbt.getLong(KEY_STAR_GROUNDED_TICK) - world.getTime() : null;
    }

    public static Long getWorldRemainingTicks(ItemStack stack, World world) {
        NbtCompound nbt = stack.getNbt();
        return world != null && nbt != null && nbt.contains(KEY_STAR_INDEX) && world instanceof StarcallerWorld sw && sw.starcaller$getStars().size() > nbt.getInt(KEY_STAR_INDEX) ? Starcaller.STAR_GROUNDED_TICKS + sw.starcaller$getStars().get(nbt.getInt(KEY_STAR_INDEX)).groundedTick - world.getTime() : null;
    }

    public static Text getCountdown(long remainingTicks) {
        return Text.translatable("item.starcaller.stardust.countdown", Text.literal(String.valueOf((remainingTicks / 20) + 1)).formatted(Formatting.GOLD)).formatted(Formatting.DARK_GRAY);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> list, TooltipContext tooltipContext) {
        NbtCompound nbt = stack.getNbt();
        super.appendTooltip(stack, world, list, tooltipContext);
        if (nbt != null) {
            Long remainingTicks = getRemainingTicks(stack, world);
            if (remainingTicks != null) {
                if (remainingTicks < 0) {
                    list.clear();
                    return;
                }
                list.add(getCountdown(remainingTicks));
            }
            if (nbt.contains(KEY_EDITOR) && nbt.contains(KEY_EDITOR_COLOR)) {
                list.add(Text.translatable("item.starcaller.stardust.editor", Text.literal(nbt.getString(KEY_EDITOR)).setStyle(Style.EMPTY.withColor(nbt.getInt(KEY_EDITOR_COLOR)))).formatted(Formatting.GRAY));
            }
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return Starcaller.TICKER.isItemBarVisible(stack);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Starcaller.TICKER.getItemBarStep(stack);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains(KEY_STAR_INDEX)) {
            return nbt.getCompound(KEY_STAR_DISPLAY).getInt(KEY_STAR_COLOR);
        }
        return super.getItemBarColor(stack);
    }

    private void forceDissipate(ItemStack stack, World world, Vec3d pos) {
        if (world instanceof ServerWorld sw) {
            sw.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 1.0F, 2.0F);
            sw.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.PLAYERS, 0.8F, 2.0F);
            sw.spawnParticles(new DustParticleEffect(ColorUtil.colorToComponents(getItemBarColor(stack)), 0.6f), pos.x, pos.y, pos.z, 40, 0.5f, 0.125f, 0.5f, 0); // shoutouts to Yttr, obviously
        }
        NbtCompound nbt = stack.getNbt();
        if (world instanceof StarcallerWorld scw && nbt != null && nbt.contains(StardustItem.KEY_STAR_INDEX)) {
            List<Star> stars = scw.starcaller$getStars();
            int starIndex = nbt.getInt(StardustItem.KEY_STAR_INDEX);
            if (starIndex < stars.size()) {
                scw.starcaller$freeStar(null, scw.starcaller$getStars().get(starIndex));
            }
        }
    }

    private void tick(ItemStack stack, World world) {
        Long remainingTicks = getRemainingTicks(stack, world);
        if (remainingTicks == null || remainingTicks <= 0) {
            stack.decrement(stack.getCount());
        }
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity playerEntity) {
        super.onCraftByPlayer(stack, world, playerEntity);
        NbtCompound nbt = stack.getNbt();
        tick(stack, world);
        Long remainingTicks = getRemainingTicks(stack, world);
        if (!stack.isEmpty() && nbt != null && nbt.contains(KEY_STAR_INDEX) && world instanceof StarcallerWorld scw && remainingTicks != null && remainingTicks > 0 && Objects.equals(remainingTicks, StardustItem.getWorldRemainingTicks(stack, world))) {
            int index = nbt.getInt(KEY_STAR_INDEX);
            if (scw.starcaller$getStars().size() > index) {
                Star star = scw.starcaller$getStars().get(index);
                scw.starcaller$colorStar(playerEntity, star, 0xFF000000 | nbt.getCompound(KEY_STAR_DISPLAY).getInt(KEY_STAR_COLOR));
            }
        }
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        tick(stack, player.getWorld());
        return super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        tick(stack, player.getWorld());
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        tick(stack, world);
        Long remainingTicks = getRemainingTicks(stack, world);
        if (world.isClient && selected && entity instanceof PlayerEntity player) {
            player.sendMessage(Text.translatable("item.starcaller.stardust.status", getCountdown(remainingTicks)).formatted(Formatting.AQUA), true);
        }
    }

    @Override
    public void blockInventoryTick(ItemStack stack, World world, BlockPos pos, int slot) {
        tick(stack, world);
    }

    @Override
    public void entityInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        TicksAlwaysItem.super.entityInventoryTick(stack, world, entity, slot, selected);
        forceDissipate(stack, world, entity.getPos());
        entity.remove(Entity.RemovalReason.DISCARDED);
    }
}
