package snownee.lightingwand.compat;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.IExtendableCraftingRecipeCategory;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import snownee.lightingwand.LW;
import snownee.lightingwand.RepairRecipe;

@JeiPlugin
public class JEICompat implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return LW.id("main");
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		IExtendableCraftingRecipeCategory craftingCategory = registration.getCraftingCategory();
		craftingCategory.addExtension(RepairRecipe.class, new RepairRecipeWrapper());
	}

	private static class RepairRecipeWrapper implements ICraftingCategoryExtension<RepairRecipe> {
		@Override
		public void setRecipe(
				RecipeHolder<RepairRecipe> recipeHolder,
				IRecipeLayoutBuilder builder,
				ICraftingGridHelper craftingGridHelper,
				IFocusGroup focuses) {
			RepairRecipe recipe = recipeHolder.value();
			builder.setShapeless();
			ItemStack broken = new ItemStack(recipe.repairable());
			int duration = broken.getMaxDamage();
			broken.setDamageValue(duration);
			craftingGridHelper.createAndSetInputs(
					builder,
					VanillaTypes.ITEM_STACK,
					List.of(List.of(broken), List.of(recipe.material().getItems())),
					0,
					0);
			ItemStack output = new ItemStack(recipe.repairable());
			output.setDamageValue(Mth.clamp(duration - Mth.ceil(duration / recipe.ratio()), 0, duration));
			craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, List.of(output));
		}
	}
}
