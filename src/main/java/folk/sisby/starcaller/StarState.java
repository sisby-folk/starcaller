package folk.sisby.starcaller;

import folk.sisby.starcaller.util.StarUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;

import java.util.List;

public class StarState extends PersistentState {
    public static final String KEY_SEED = "seed";
    public static final String KEY_LIMIT = "limit";
    public static final String KEY_STARS = "stars";

    public long seed;
    public int limit;
    public int iterations; // Not persistent, just convenient for the client.
    public List<Star> stars;

    public StarState(long worldSeed) {
        this.seed = (Starcaller.CONFIG.starSeed != -1 ? Starcaller.CONFIG.starSeed : worldSeed);
        this.limit = Starcaller.CONFIG.starLimit;
        this.iterations = StarUtil.getGeneratorIterations(seed, limit);
        this.stars = StarUtil.generateStars(this.seed, this.iterations);
        markDirty();
    }

    public StarState(long seed, int limit, int iterations, List<Star> stars) {
        this.seed = seed;
        this.limit = limit;
        this.iterations = iterations;
        this.stars = stars;
    }

    public static StarState fromNbt(NbtCompound nbt, long worldSeed) {
        long seed = nbt.contains(KEY_SEED) ? nbt.getLong(KEY_SEED) : (Starcaller.CONFIG.starSeed != -1 ? Starcaller.CONFIG.starSeed : worldSeed);
        int limit = nbt.contains(KEY_LIMIT) ? nbt.getInt(KEY_LIMIT) : Starcaller.CONFIG.starLimit;
        int iterations = StarUtil.getGeneratorIterations(seed, limit);
        List<Star> stars = StarUtil.generateStars(seed, iterations);
        int i = 0;
        for (NbtElement starElement : nbt.getList(KEY_STARS, NbtElement.COMPOUND_TYPE)) {
            if (i < stars.size() && starElement instanceof NbtCompound starCompound) {
                stars.get(i).readNbt(starCompound);
            }
            i++;
        }
        return new StarState(seed, limit, iterations, stars);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (Star star : stars) {
            nbtList.add(star.toNbt());
        }
        nbt.putLong(KEY_SEED, this.seed);
        nbt.putInt(KEY_LIMIT, this.limit);
        nbt.put(KEY_STARS, nbtList);
        return nbt;
    }
}
