package snownee.lightingwand.compat;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.IExtendableCraftingRecipeCategory;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import snownee.lightingwand.LW;
import snownee.lightingwand.RepairRecipe;

@JeiPlugin
public class JEICompat implements IModPlugin {

	@Override
	public Identifier getPluginUid() {
		return LW.id("main");
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		IExtendableCraftingRecipeCategory craftingCategory = registration.getCraftingCategory();
		craftingCategory.addExtension(RepairRecipe.class, new RepairRecipeWrapper());
	}

	private static class RepairRecipeWrapper implements ICraftingCategoryExtension<RepairRecipe> {
		@Override
		public List<SlotDisplay> getIngredients(RecipeHolder<RepairRecipe> recipeHolder) {
			return List.of(
					new SlotDisplay.ItemStackSlotDisplay(recipeHolder.value().repairable()),
					recipeHolder.value().material().display());
		}

		@Override
		public void setRecipe(
				RecipeHolder<RepairRecipe> recipeHolder,
				IRecipeLayoutBuilder builder,
				ICraftingGridHelper craftingGridHelper,
				IFocusGroup focuses) {
			RepairRecipe recipe = recipeHolder.value();
			builder.setShapeless();
			ItemStack broken = recipe.repairable().create();
			int duration = broken.getMaxDamage();
			broken.setDamageValue(duration);
			craftingGridHelper.createAndSetIngredientsFromDisplays(
					builder,
					List.of(new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(broken)), recipe.material().display()),
					0,
					0);
			ItemStack output = recipe.repairable().create();
			output.setDamageValue(Mth.clamp(duration - Mth.ceil(duration / recipe.ratio()), 0, duration));
			craftingGridHelper.createAndSetOutputs(builder, List.of(output));
		}
	}
}
