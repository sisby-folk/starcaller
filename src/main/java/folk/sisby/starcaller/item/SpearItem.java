package folk.sisby.starcaller.item;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.duck.StarcallerWorld;
import folk.sisby.starcaller.util.StarUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.List;

public class SpearItem extends Item {
    public static final int DRAW_TIME = 10;

    public SpearItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        playerEntity.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, World world, Entity entity, int delta, boolean bl) {
        super.inventoryTick(itemStack, world, entity, delta, bl);
        if (world.isClient && entity instanceof PlayerEntity player && world instanceof StarcallerWorld scw && (player.getMainHandStack() == itemStack || player.getOffHandStack() == itemStack)) {
            if (player.raycast(12 * 16, 1.0F, false).getType() == HitResult.Type.MISS) {
                List<Star> stars = scw.starcaller$getStars();
                Vec3d cursorCoordinates = StarUtil.correctForSkyAngle(StarUtil.getStarCursor(player.getHeadYaw(), player.getPitch()), world.getSkyAngle(1.0F));
                Star closestStar = stars.stream().filter(s -> s.groundedTick == -1 || s.groundedTick + Starcaller.STAR_GROUNDED_TICKS < world.getTime()).filter(s -> s.groundedTick == -1 || s.groundedTick + Starcaller.STAR_GROUNDED_TICKS < world.getTime()).min(Comparator.comparingDouble(s -> s.pos.squaredDistanceTo(cursorCoordinates))).get();
                if (cursorCoordinates.isInRange(closestStar.pos, 4)) {
                    int i = stars.indexOf(closestStar);
                    player.sendMessage(Text.translatable("messages.starcaller.star.info", Text.translatable("star.starcaller.overworld.%s".formatted(i)).setStyle(Style.EMPTY.withFormatting(Formatting.ITALIC).withColor(closestStar.color))), true);
                    return;
                }
                player.sendMessage(Text.empty(), true);
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
        if (livingEntity instanceof PlayerEntity player) {
            int j = this.getMaxUseTime(itemStack) - i;
            if (j >= DRAW_TIME) {
                world.playSoundFromEntity(player, player, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (world instanceof StarcallerWorld scw) {
                    if (player.raycast(12 * 16, 1.0F, false).getType() == HitResult.Type.MISS) {
                        Vec3d cursorCoordinates = StarUtil.correctForSkyAngle(StarUtil.getStarCursor(player.getHeadYaw(), player.getPitch()), world.getSkyAngle(1.0F));
                        Star closestStar = ((StarcallerWorld) world).starcaller$getStars().stream().min(Comparator.comparingDouble(s -> s.pos.squaredDistanceTo(cursorCoordinates))).get();
                        if (cursorCoordinates.isInRange(closestStar.pos, 4)) {
                            int starIndex = ((StarcallerWorld) world).starcaller$getStars().indexOf(closestStar);
                            scw.starcaller$groundStar(player, closestStar);
                            player.getInventory().offerOrDrop(StardustItem.fromStar(starIndex, closestStar));
                            return;
                        }
                        return;
                    }
                }
                player.incrementStat(Stats.USED.getOrCreateStat(this));
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack itemStack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack itemStack) {
        return 72000;
    }
}
