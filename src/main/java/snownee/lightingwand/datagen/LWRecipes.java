package snownee.lightingwand.datagen;

import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NonNull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.CustomCraftingRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import snownee.kiwi.recipe.RecipeUtil;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.RepairRecipe;

public class LWRecipes extends RecipeProvider {
	public LWRecipes(HolderLookup.Provider registries, RecipeOutput output) {
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

		CustomCraftingRecipeBuilder.customCrafting(
						RecipeCategory.MISC, (
								(commonInfo, bookInfo) -> new RepairRecipe(
										commonInfo,
										bookInfo,
										Ingredient.of(CoreModule.WAND.get()),
										RecipeUtil.tagIngredient(
												registries.lookupOrThrow(Registries.ITEM),
												Tags.Items.DUSTS_GLOWSTONE),
										4)))
				.unlockedBy(getHasName(CoreModule.WAND.get()), has(CoreModule.WAND.get()))
				.save(output, "repair");

		dyedItem(CoreModule.WAND.get(), "dyed_lighting_wand");
	}

	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
			super(output, registries);
		}

		@Override
		protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
			return new LWRecipes(registries, output);
		}

		@Override
		public @NonNull String getName() {
			return "LW Recipes";
		}
	}
}
