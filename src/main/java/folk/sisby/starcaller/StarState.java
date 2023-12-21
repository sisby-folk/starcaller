package folk.sisby.starcaller;

import folk.sisby.starcaller.util.StarUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;

import java.util.List;

public class StarState extends PersistentState {
    public List<Star> stars;

    public static PersistentState.Type<StarState> getPersistentStateType() {
        return new PersistentState.Type<>(StarState::new, StarState::fromNbt, null);
    }

    public StarState() {
        this.stars = StarUtil.generateStars(Starcaller.STAR_SEED);
    }

    public StarState(List<Star> stars) {
        this.stars = stars;
    }

    public static StarState fromNbt(NbtCompound nbt) {
        List<Star> list = StarUtil.generateStars(Starcaller.STAR_SEED);
        int i = 0;
        for (NbtElement starElement : nbt.getList("stars", NbtElement.COMPOUND_TYPE)) {
            if (i < list.size() && starElement instanceof NbtCompound starCompound) {
                list.get(i).readNbt(starCompound);
            }
            i++;
        }
        return new StarState(list);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (Star star : stars) {
            nbtList.add(star.toNbt());
        }
        nbt.put("stars", nbtList);
        return nbt;
    }
}
