package folk.sisby.starcaller.item;

import com.unascribed.lib39.sandman.api.TicksAlwaysItem;
import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.StarComponent;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.duck.StarcallerWorld;
import folk.sisby.starcaller.util.ColorUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
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
import java.util.Optional;

public class StardustItem extends Item implements TicksAlwaysItem {
    public StardustItem(Settings settings) {
        super(settings);
    }

    public static ItemStack fromStar(int index, Star star) {
        ItemStack stack = Starcaller.STARDUST.getDefaultStack().copy();
		stack.set(Starcaller.STAR, new StarComponent(
			index,
			star.groundedTick,
			Optional.ofNullable(star.editor),
			star.editorColor
		));
		stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(star.color, true));
        return stack;
    }

    public static @Nullable Integer getStarIndex(ItemStack stack) {
        return stack.contains(Starcaller.STAR) ? stack.get(Starcaller.STAR).starIndex() : null;
    }

    public static @Nullable Long getGroundedTick(ItemStack stack) {
        return stack.contains(Starcaller.STAR) ? stack.get(Starcaller.STAR).groundedTick() : null;
    }

    public static @Nullable MutableText getEditor(ItemStack stack) {
        return stack.contains(Starcaller.STAR) && stack.get(Starcaller.STAR).editor().isPresent() ? Text.literal(stack.get(Starcaller.STAR).editor().get()).setStyle(Style.EMPTY.withColor(stack.get(Starcaller.STAR).editorColor())) : null;
    }

    public static @Nullable Star getStar(ItemStack stack, World world) {
        Integer starIndex = getStarIndex(stack);
        if (world instanceof StarcallerWorld scw && starIndex != null) {
            List<Star> stars = scw.starcaller$getStars();
            if (starIndex < stars.size()) {
                return stars.get(starIndex);
            }
        }
        return null;
    }

    public static @Nullable Long getRemainingTicks(ItemStack stack, World world) {
        Long groundedTick = getGroundedTick(stack);
        return world != null && groundedTick != null ? Starcaller.CONFIG.starGroundedTicks + groundedTick - world.getTime() : null;
    }

    public static @Nullable Long getWorldRemainingTicks(ItemStack stack, World world) {
        Star star = getStar(stack, world);
        return star != null ? Starcaller.CONFIG.starGroundedTicks + star.groundedTick - world.getTime() : null;
    }

    public static Text getCountdown(long remainingTicks) {
        return Text.translatable("item.starcaller.stardust.countdown", Text.literal(String.valueOf((int) Math.ceil(remainingTicks / 20.0F))).formatted(Formatting.GOLD)).formatted(Formatting.DARK_GRAY);
    }

    @Override
    public Text getName(ItemStack stack) {
        MutableText name = super.getName(stack).copy();
        Integer starIndex = getStarIndex(stack);
        if (starIndex != null) {
            name = Text.translatable("star.starcaller.overworld.%s".formatted(starIndex)).formatted(Formatting.ITALIC);
        }
        if (stack.contains(DataComponentTypes.DYED_COLOR)) {
            name = name.styled(style -> style.withColor(stack.get(DataComponentTypes.DYED_COLOR).rgb()));
        }
        return starIndex != null ? Text.translatable("item.starcaller.stardust.named", name).formatted(Formatting.GRAY) : name;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        MutableText editor = getEditor(stack);
        if (editor != null) {
	        tooltip.add(Text.translatable("item.starcaller.stardust.editor", editor).formatted(Formatting.GRAY));
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
        if (stack.contains(DataComponentTypes.DYED_COLOR)) {
            return stack.get(DataComponentTypes.DYED_COLOR).rgb();
        }
        return 0xFFFFFF;
    }

    private void dissipateEffect(ItemStack stack, World world, Vec3d pos, int count) {
        if (world instanceof ServerWorld sw) {
            sw.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 3.0F, 2.0F);
            sw.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ITEM_BRUSH_BRUSHING_GENERIC, SoundCategory.PLAYERS, 0.8F, 2.0F);
            sw.spawnParticles(new DustParticleEffect(ColorUtil.colorToComponents(getItemBarColor(stack)), 0.6f), pos.x, pos.y, pos.z, count, 0.5f, 0.125f, 0.5f, 0); // shoutouts to Yttr, obviously
        }
    }

    private void forceDissipate(ItemStack stack, World world, Vec3d pos) {
        dissipateEffect(stack, world, pos, 40);
        Star star = getStar(stack, world);
        if (world instanceof StarcallerWorld scw && star != null) {
            scw.starcaller$freeStar(null, star);
        }
    }

    private void tick(ItemStack stack, World world, Vec3d pos) {
        Long remainingTicks = getRemainingTicks(stack, world);
        if (remainingTicks == null || remainingTicks <= 0) {
            dissipateEffect(stack, world, pos, 20);
            stack.decrement(stack.getCount());
        }
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity playerEntity) {
        super.onCraftByPlayer(stack, world, playerEntity);
        Long remainingTicks = getRemainingTicks(stack, world);
        Star star = getStar(stack, world);
        if (world instanceof StarcallerWorld scw && star != null && remainingTicks != null && remainingTicks > 0) {
            if (!world.isClient && !Objects.equals(remainingTicks, StardustItem.getWorldRemainingTicks(stack, world))) return;
            scw.starcaller$colorStar(playerEntity, star, 0xFF000000 | stack.get(DataComponentTypes.DYED_COLOR).rgb());
        }
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        tick(stack, player.getWorld(), player.getEyePos());
        return super.onStackClicked(stack, slot, clickType, player);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        tick(stack, player.getWorld(), player.getEyePos());
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        tick(stack, world, entity.getEyePos());
        Long remainingTicks = getRemainingTicks(stack, world);
        if (world.isClient && selected && entity instanceof PlayerEntity player && remainingTicks != null) {
            player.sendMessage(Text.translatable("item.starcaller.stardust.status", getCountdown(remainingTicks)).formatted(Formatting.AQUA), true);
        }
    }

    @Override
    public void blockInventoryTick(ItemStack stack, World world, BlockPos pos, int slot) {
        tick(stack, world, pos.toCenterPos());
    }

    @Override
    public void entityInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        TicksAlwaysItem.super.entityInventoryTick(stack, world, entity, slot, selected);
        forceDissipate(stack, world, entity.getPos());
        entity.remove(Entity.RemovalReason.DISCARDED);
    }
}
