package folk.sisby.starcaller;

import folk.sisby.starcaller.util.StarUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;

import java.util.List;

public class StarState extends PersistentState {
    public static final String KEY_SEED = "seed";
    public static final String KEY_ITERATIONS = "iterations";
    public static final String KEY_STARS = "stars";

    public long seed;
    public int iterations;
    public List<Star> stars;

    public static PersistentState.Type<StarState> getPersistentStateType() {
        return new PersistentState.Type<>(StarState::new, StarState::fromNbt, null);
    }

    public StarState() {
        this.seed = Starcaller.STAR_SEED;
        this.iterations = Starcaller.STAR_ITERATIONS;
        this.stars = StarUtil.generateStars(this.seed, this.iterations);
    }

    public StarState(long seed, int iterations, List<Star> stars) {
        this.seed = seed;
        this.iterations = iterations;
        this.stars = stars;
    }

    public static StarState fromNbt(NbtCompound nbt) {
        long seed = nbt.contains(KEY_SEED) ? nbt.getLong(KEY_SEED) : Starcaller.STAR_SEED;
        int iterations = nbt.contains(KEY_ITERATIONS) ? nbt.getInt(KEY_ITERATIONS) : Starcaller.STAR_ITERATIONS;
        List<Star> stars = StarUtil.generateStars(seed, iterations);
        int i = 0;
        for (NbtElement starElement : nbt.getList(KEY_STARS, NbtElement.COMPOUND_TYPE)) {
            if (i < stars.size() && starElement instanceof NbtCompound starCompound) {
                stars.get(i).readNbt(starCompound);
            }
            i++;
        }
        return new StarState(seed, iterations, stars);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (Star star : stars) {
            nbtList.add(star.toNbt());
        }
        nbt.putLong(KEY_SEED, this.seed);
        nbt.putLong(KEY_ITERATIONS, this.iterations);
        nbt.put(KEY_STARS, nbtList);
        return nbt;
    }
}
