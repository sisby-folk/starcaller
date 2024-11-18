package folk.sisby.starcaller.packet;

import folk.sisby.starcaller.Starcaller;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;

public record S2CInitialStarState(long seed, int iterations, Map<Integer, Long> groundedStars, Map<Integer, Integer> starColors) implements CustomPayload {
	public static Id<S2CInitialStarState> ID = new Id<>(Starcaller.id("initial_star_state"));
	public static PacketCodec<PacketByteBuf, S2CInitialStarState> CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_LONG, S2CInitialStarState::seed,
		PacketCodecs.VAR_INT, S2CInitialStarState::iterations,
		PacketCodecs.map(HashMap::new, PacketCodecs.VAR_INT, PacketCodecs.VAR_LONG), S2CInitialStarState::groundedStars,
		PacketCodecs.map(HashMap::new, PacketCodecs.VAR_INT, PacketCodecs.VAR_INT), S2CInitialStarState::starColors,
		S2CInitialStarState::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
