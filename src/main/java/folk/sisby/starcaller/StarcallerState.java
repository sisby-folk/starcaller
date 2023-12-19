package folk.sisby.starcaller;

import folk.sisby.starcaller.util.StarUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;

public class StarcallerState extends PersistentState {
    public List<StarcallerStar> stars;

    public static PersistentState.Type<StarcallerState> getPersistentStateType() {
        return new PersistentState.Type<>(StarcallerState::new, StarcallerState::fromNbt, null);
    }

    public StarcallerState() {
        this.stars = StarUtil.generateStars(10842L);
    }

    public StarcallerState(List<StarcallerStar> stars) {
        this.stars = stars;
    }

    public static StarcallerState fromNbt(NbtCompound compound) {
        List<StarcallerStar> list = new ArrayList<>();
        return new StarcallerState(list);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return nbt;
    }
}
