package snownee.lightingwand.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import snownee.lightingwand.CoreModule;

public class LWItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {

	public LWItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
		super(output, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		valueLookupBuilder(ConventionalItemTags.TOOLS).add(CoreModule.WAND.get());
	}
}
