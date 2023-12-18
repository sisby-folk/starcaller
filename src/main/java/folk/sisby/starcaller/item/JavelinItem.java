package folk.sisby.starcaller.item;

import folk.sisby.starcaller.entity.JavelinEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class JavelinItem extends Item {
    public static final int DRAW_TIME = 10;
    public static final float THROW_VELOCITY = 25.0F;

    public JavelinItem(Settings settings) {
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
        if (livingEntity instanceof PlayerEntity playerEntity) {
            int j = this.getMaxUseTime(itemStack) - i;
            if (j >= DRAW_TIME) {
                if (!world.isClient) {
                    JavelinEntity entity = new JavelinEntity(world, playerEntity, itemStack);
                    entity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, THROW_VELOCITY, 1.0F);
                    world.spawnEntity(entity);
                    world.playSoundFromEntity(null, entity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    if (!playerEntity.getAbilities().creativeMode) {
                        playerEntity.getInventory().removeOne(itemStack);
                    }
                }
                playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
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
