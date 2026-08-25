package snownee.lightingwand.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import snownee.kiwi.datagen.KiwiBlockTagsProvider;

public class LWBlockTags extends KiwiBlockTagsProvider {

	public LWBlockTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
		super(output, registryLookupFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
	}
}
