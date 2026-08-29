package snownee.lightingwand;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public record WithDamageSlotDisplay(SlotDisplay source, double ratio) implements SlotDisplay {
	public static final MapCodec<WithDamageSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
					SlotDisplay.CODEC.fieldOf("source").forGetter(WithDamageSlotDisplay::source),
					Codec.DOUBLE.fieldOf("ratio").forGetter(WithDamageSlotDisplay::ratio))
			.apply(instance, WithDamageSlotDisplay::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WithDamageSlotDisplay> STREAM_CODEC = StreamCodec.composite(
			SlotDisplay.STREAM_CODEC,
			WithDamageSlotDisplay::source,
			ByteBufCodecs.DOUBLE,
			WithDamageSlotDisplay::ratio,
			WithDamageSlotDisplay::new);

	@Override
	public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> builder) {
		return builder instanceof DisplayContentsFactory.ForStacks<T> stacks
				? this.source.resolve(context, SlotDisplay.ItemStackContentsFactory.INSTANCE)
				.peek(s -> s.setDamageValue((int) (s.getMaxDamage() * ratio)))
				.map(stacks::forStack)
				: Stream.empty();
	}

	@Override
	public Type<? extends SlotDisplay> type() {
		return CoreModule.WITH_DAMAGE.get();
	}
}
