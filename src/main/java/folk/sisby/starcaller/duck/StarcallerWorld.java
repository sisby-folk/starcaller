package folk.sisby.starcaller.duck;

import folk.sisby.starcaller.Star;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public interface StarcallerWorld {
    long starcaller$getSeed();
    int starcaller$getIterations();
    List<Star> starcaller$getStars();

    void starcaller$setSeed(long seed);
    void starcaller$setIterations(int iterations);

    void starcaller$groundStar(PlayerEntity cause, Star star);

    void starcaller$freeStar(PlayerEntity cause, Star star);

    void starcaller$colorStar(PlayerEntity cause, Star star, int color);
}
