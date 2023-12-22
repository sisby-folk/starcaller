package folk.sisby.starcaller.mixin;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.StarState;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.duck.StarcallerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld implements StarcallerWorld {
    @Shadow public abstract PersistentStateManager getPersistentStateManager();

    @Override
    public List<Star> starcaller$getStars() {
        StarState state = getPersistentStateManager().get(StarState.getPersistentStateType(), Starcaller.STATE_KEY);
        return state != null ? state.stars : List.of();
    }

    @Override
    public void starcaller$groundStar(PlayerEntity cause, Star star) {
        Starcaller.groundStar(cause, ((ServerWorld) (Object) this), star);
    }

    @Override
    public void starcaller$freeStar(PlayerEntity cause, Star star) {
        Starcaller.freeStar(cause, ((ServerWorld) (Object) this), star);
    }

    @Override
    public void starcaller$colorStar(PlayerEntity cause, Star star, int color) {
        Starcaller.colorStar(cause, ((ServerWorld) (Object) this), star, color);
    }
}
