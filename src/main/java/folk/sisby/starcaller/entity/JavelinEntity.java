package folk.sisby.starcaller.entity;

import folk.sisby.starcaller.Starcaller;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class JavelinEntity extends PersistentProjectileEntity {
    private static final ItemStack DEFAULT_STACK = new ItemStack(Starcaller.JAVELIN);

    public JavelinEntity(EntityType<? extends JavelinEntity> entityType, World world) {
        super(entityType, world, DEFAULT_STACK);
    }

    public JavelinEntity(World world, LivingEntity livingEntity, ItemStack itemStack) {
        super(Starcaller.JAVELIN_ENTITY, livingEntity, world, itemStack);
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (entity != null) {
            if (!this.isOwnerAlive()) {
                if (!this.getWorld().isClient && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1F);
                }
                this.discard();
            } else {
                this.setNoClip(true);
                Vec3d vec3d = entity.getEyePos().subtract(this.getPos());
                this.setPos(this.getX(), this.getY() + vec3d.y, this.getZ());
                if (this.getWorld().isClient) {
                    this.lastRenderY = this.getY();
                }

                this.setVelocity(this.getVelocity().multiply(0.95).add(vec3d.normalize()));
            }
        }

        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        if (entity == null || !entity.isAlive()) {
            return false;
        } else {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        }
    }

    @Override
    protected boolean tryPickup(PlayerEntity playerEntity) {
        if (super.tryPickup(playerEntity) || this.isNoClip() && this.isOwner(playerEntity) && playerEntity.getInventory().insertStack(this.asItemStack())) {
            playerEntity.getInventory().insertStack(Starcaller.STARDUST.getDefaultStack());
            return true;
        }
        return false;
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void onPlayerCollision(PlayerEntity playerEntity) {
        if (this.isOwner(playerEntity) || this.getOwner() == null) {
            super.onPlayerCollision(playerEntity);
        }
    }

    @Override
    protected float getDragInWater() {
        return 1.0F;
    }

    @Override
    public boolean shouldRender(double d, double e, double f) {
        return true;
    }
}
