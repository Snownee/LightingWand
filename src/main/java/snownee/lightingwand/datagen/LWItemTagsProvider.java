package snownee.lightingwand.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import snownee.lightingwand.CoreModule;

public class LWItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {

	public LWItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
		super(output, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		valueLookupBuilder(ItemTags.DYEABLE).add(CoreModule.WAND.get());
	}
}
