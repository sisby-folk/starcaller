package folk.sisby.starcaller.item;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.util.StarUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;

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
    public void usageTick(World world, LivingEntity livingEntity, ItemStack itemStack, int i) {
        super.usageTick(world, livingEntity, itemStack, i);
    }

    @Override
    public void onStoppedUsing(ItemStack itemStack, World world, LivingEntity livingEntity, int i) {
        if (livingEntity instanceof PlayerEntity player) {
            int j = this.getMaxUseTime(itemStack) - i;
            if (j >= DRAW_TIME) {
                world.playSoundFromEntity(player, player, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!world.isClient) {
                    if (player.raycast(world.getServer().getPlayerManager().getViewDistance() * 16, 1.0F, false).getType() == HitResult.Type.MISS) {
                        Vec3d cursorCoordinates = StarUtil.correctForSkyAngle(StarUtil.getStarCursor(player.getHeadYaw(), player.getPitch()), world.getSkyAngle(1.0F));
                        Star closestStar = Starcaller.STATE.stars.stream().min(Comparator.comparingDouble(s -> s.pos.squaredDistanceTo(cursorCoordinates))).get();
                        if (cursorCoordinates.isInRange(closestStar.pos, 4)) {
                            int starIndex = Starcaller.STATE.stars.indexOf(closestStar);
                            ItemStack stack = Starcaller.STARDUST.getDefaultStack();
                            stack.setCustomName(Text.translatable("item.starcaller.stardust.named", Text.translatable("star.starcaller.overworld.%s".formatted(starIndex))));
                            player.getInventory().offerOrDrop(stack);
                            player.getInventory().removeOne(itemStack);
                            Starcaller.groundStar(null, (ServerWorld) world, closestStar);
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
