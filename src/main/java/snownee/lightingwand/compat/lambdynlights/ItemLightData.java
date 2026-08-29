package snownee.lightingwand.compat.lambdynlights;

import java.util.concurrent.CompletableFuture;

import dev.lambdaurora.lambdynlights.api.data.ItemLightSourceDataProvider;
import dev.lambdaurora.lambdynlights.api.item.ItemLuminance;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.LightValuePredicate;

public class ItemLightData extends ItemLightSourceDataProvider {
	public ItemLightData(
			PackOutput packOutput,
			CompletableFuture<HolderLookup.Provider> registryProvider,
			String defaultNamespace) {
		super(packOutput, registryProvider, defaultNamespace);
	}

	@Override
	protected void generate(Context context) {
		for (int i = 1; i < 16; i++) {
			DataComponentMatchers components = DataComponentMatchers.Builder.components().partial(
					CoreModule.LIGHT_VALUE_PREDICATE.get(),
					new LightValuePredicate(MinMaxBounds.Ints.exactly(i))).build();
			ItemPredicate predicate = ItemPredicate.Builder.item()
					.of(context.itemLookup(), CoreModule.WAND.get())
					.withComponents(components)
					.build();
			context.add("wand_%s".formatted(i), predicate, ItemLuminance.of(i), false);
		}
	}
}
