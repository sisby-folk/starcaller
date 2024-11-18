package folk.sisby.starcaller;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record StarComponent(int starIndex, long groundedTick, Optional<String> editor, int editorColor) {
    public static final Codec<StarComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("starIndex").forGetter(StarComponent::starIndex),
        Codec.LONG.fieldOf("groundedTick").forGetter(StarComponent::groundedTick),
        Codec.STRING.optionalFieldOf("editor").forGetter(StarComponent::editor),
	    Codec.INT.fieldOf("editorColor").forGetter(StarComponent::editorColor)
    ).apply(instance, StarComponent::new));

	public StarComponent withEditor(String editor, int editorColor) {
		return new StarComponent(starIndex, groundedTick, Optional.of(editor), editorColor);
	}
}
