package folk.sisby.starcaller.mixin;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.duck.StarcallerWorld;
import folk.sisby.starcaller.item.StardustItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {
    @Inject(method = "tick", at = @At("HEAD"))
    public void stardustTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack stack = self.getStack();
        if (stack.isOf(Starcaller.STARDUST)) { // Replace me with sandman
            self.getWorld().playSoundFromEntity(self, SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.PLAYERS, 1.0F, 1.0F);
            NbtCompound nbt = stack.getNbt();
            if (self.getWorld() instanceof StarcallerWorld scw && nbt != null && nbt.contains(StardustItem.KEY_STAR_INDEX)) {
                List<Star> stars = scw.starcaller$getStars();
                int starIndex = nbt.getInt(StardustItem.KEY_STAR_INDEX);
                if (starIndex < stars.size()) {
                    scw.starcaller$freeStar(null, scw.starcaller$getStars().get(starIndex));
                }
            }
            self.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
