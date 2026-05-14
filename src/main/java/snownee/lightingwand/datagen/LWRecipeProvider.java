package snownee.lightingwand.datagen;

import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NonNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import snownee.lightingwand.CoreModule;

public class LWRecipeProvider extends RecipeProvider {
	public LWRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	@Override
	protected void buildRecipes() {
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

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
			super(output, registries);
		}

		@Override
		protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
			return new LWRecipeProvider(registries, output);
		}

		@Override
		public @NonNull String getName() {
			return "LW Recipes";
		}
	}
}
