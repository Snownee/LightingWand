package snownee.lightingwand.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import snownee.lightingwand.CoreModule;

public class LWRecipeProvider extends FabricRecipeProvider {
	public LWRecipeProvider(
			FabricPackOutput output,
			CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		return new RecipeProvider(registries, output) {
			@Override
			public void buildRecipes() {
				shaped(RecipeCategory.MISC, CoreModule.WAND.get())
						.pattern("  *")
						.pattern(" / ")
						.pattern("/  ")
						.define('*', Blocks.GLOWSTONE)
						.define('/', Items.BLAZE_ROD)
						.unlockedBy(getHasName(Blocks.GLOWSTONE), has(Blocks.GLOWSTONE))
						.unlockedBy(getHasName(Items.BLAZE_ROD), has(Items.BLAZE_ROD))
						.save(output);
			}
		};
	}

	@Override
	public String getName() {
		return "LW Recipes";
	}
}
