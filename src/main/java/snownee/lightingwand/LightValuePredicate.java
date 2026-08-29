package snownee.lightingwand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;

public record LightValuePredicate(MinMaxBounds.Ints light) implements SingleComponentItemPredicate<WandItemData> {
	public static final Codec<LightValuePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MinMaxBounds.Ints.CODEC.fieldOf("light").forGetter(LightValuePredicate::light)
	).apply(instance, LightValuePredicate::new));

	@Override
	public DataComponentType<WandItemData> componentType() {
		return CoreModule.WAND_ITEM_DATA.get();
	}

	@Override
	public boolean matches(WandItemData value) {
		return light.matches(value.light());
	}
}
