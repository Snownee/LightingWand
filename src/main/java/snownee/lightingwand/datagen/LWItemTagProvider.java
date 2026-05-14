package snownee.lightingwand.datagen;

import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NonNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.LW;

public class LWItemTagProvider extends ItemTagsProvider {
	private static final TagKey<Item> DYEABLE = TagKey.create(Registries.ITEM, LW.id("dyeable"));

	public LWItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(output, completableFuture, LW.ID);
	}

	@Override
	protected void addTags(HolderLookup.@NonNull Provider wrapperLookup) {
		tag(DYEABLE).add(CoreModule.WAND.get());
	}
}
