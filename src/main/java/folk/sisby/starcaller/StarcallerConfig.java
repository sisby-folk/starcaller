package folk.sisby.starcaller;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;

public class StarcallerConfig extends WrappedConfig {
    @Comment("The seed used by star generation.")
    @Comment("The default seed generates identical stars to vanilla.")
    @Comment("To change the seed or limit of an existing world, delete data/starcaller_stars.dat")
    @Comment("To keep existing colorings (but not positions), edit the values in the file manually.")
    @Comment("Set to -1 to use the world seed.")
    public final Long starSeed = 10842L;

    @Comment("The maximum amount of stars the generator may place.")
    @Comment("The generator will iterate 3x of the limit, so less may appear.")
    @Comment("Star names are provided via indexed translations e.g. star.starcaller.overworld.11")
    @Comment("Starcaller provides \"proper names\" up to 780 stars, and fake bayer designations up to 7468 stars.")
    public final Integer starLimit = 780;
}
