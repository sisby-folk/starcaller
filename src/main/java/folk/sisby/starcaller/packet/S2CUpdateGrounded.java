package folk.sisby.starcaller.packet;

import folk.sisby.starcaller.Starcaller;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;

public record S2CUpdateGrounded(Map<Integer, Long> groundedStars) implements CustomPayload {
	public static Id<S2CUpdateGrounded> ID = new Id<>(Starcaller.id("update_grounded"));
	public static PacketCodec<PacketByteBuf, S2CUpdateGrounded> CODEC = PacketCodec.tuple(
		PacketCodecs.map(HashMap::new, PacketCodecs.VAR_INT, PacketCodecs.VAR_LONG), S2CUpdateGrounded::groundedStars,
		S2CUpdateGrounded::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
