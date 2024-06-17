package snownee.lightingwand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record WandItemData(int light, float alpha) {
	public static final Codec<WandItemData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.intRange(1, 15).optionalFieldOf("light", 15).forGetter(WandItemData::light),
			Codec.floatRange(0, 1).optionalFieldOf("alpha", 1F).forGetter(WandItemData::alpha)
	).apply(instance, WandItemData::new));
	public static final StreamCodec<ByteBuf, WandItemData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			WandItemData::light,
			ByteBufCodecs.FLOAT,
			WandItemData::alpha,
			WandItemData::new);
	public static final WandItemData DEFAULT = new WandItemData(15, 1F);
}
