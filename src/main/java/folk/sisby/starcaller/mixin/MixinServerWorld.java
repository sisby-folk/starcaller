package folk.sisby.starcaller.mixin;

import folk.sisby.starcaller.Star;
import folk.sisby.starcaller.StarState;
import folk.sisby.starcaller.Starcaller;
import folk.sisby.starcaller.duck.StarcallerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld implements StarcallerWorld {
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

    @Override
    public long starcaller$getSeed() {
        ServerWorld self = (ServerWorld) (Object) this;
        StarState state = self.getPersistentStateManager().get(StarState.getPersistentStateType(), Starcaller.STATE_KEY);
        return state != null ? state.seed : Starcaller.STAR_SEED;
    }

    @Override
    public int starcaller$getIterations() {
        ServerWorld self = (ServerWorld) (Object) this;
        StarState state = self.getPersistentStateManager().get(StarState.getPersistentStateType(), Starcaller.STATE_KEY);
        return state != null ? state.iterations : Starcaller.STAR_ITERATIONS;
    }

    @Override
    public List<Star> starcaller$getStars() {
        ServerWorld self = (ServerWorld) (Object) this;
        StarState state = self.getPersistentStateManager().get(StarState.getPersistentStateType(), Starcaller.STATE_KEY);
        return state != null ? state.stars : List.of();
    }

    @Override
    public void starcaller$setSeed(long seed) {
        ServerWorld self = (ServerWorld) (Object) this;
        StarState state = self.getPersistentStateManager().get(StarState.getPersistentStateType(), Starcaller.STATE_KEY);
        if (state != null) {
            state.seed = seed;
            state.markDirty();
        }
    }

    @Override
    public void starcaller$setIterations(int iterations) {
        ServerWorld self = (ServerWorld) (Object) this;
        StarState state = self.getPersistentStateManager().get(StarState.getPersistentStateType(), Starcaller.STATE_KEY);
        if (state != null) {
            state.iterations = iterations;
            state.markDirty();
        }
    }
}
