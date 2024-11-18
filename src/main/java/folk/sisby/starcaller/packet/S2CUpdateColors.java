package folk.sisby.starcaller.packet;

import folk.sisby.starcaller.Starcaller;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.HashMap;
import java.util.Map;

public record S2CUpdateColors(Map<Integer, Integer> starColors) implements CustomPayload {
	public static Id<S2CUpdateColors> ID = new Id<>(Starcaller.id("update_colors"));
	public static PacketCodec<PacketByteBuf, S2CUpdateColors> CODEC = PacketCodec.tuple(
		PacketCodecs.map(HashMap::new, PacketCodecs.VAR_INT, PacketCodecs.VAR_INT), S2CUpdateColors::starColors,
		S2CUpdateColors::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
