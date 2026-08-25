package snownee.lightingwand.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.CustomCraftingRecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import snownee.kiwi.recipe.RecipeUtil;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.RepairRecipe;

public class LWRecipes extends FabricRecipeProvider {
	public LWRecipes(
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

				CustomCraftingRecipeBuilder.customCrafting(
								RecipeCategory.MISC, (
										(commonInfo, bookInfo) -> new RepairRecipe(
												commonInfo,
												bookInfo,
												Ingredient.of(CoreModule.WAND.get()),
												RecipeUtil.tagIngredient(
														registries.lookupOrThrow(Registries.ITEM),
														ConventionalItemTags.GLOWSTONE_DUSTS),
												4)))
						.unlockedBy(getHasName(CoreModule.WAND.get()), has(CoreModule.WAND.get()))
						.save(output, "repair");

				dyedItem(CoreModule.WAND.get(), "dyed_lighting_wand");
			}
		};
	}

	@Override
	public String getName() {
		return "LW Recipes";
	}
}
