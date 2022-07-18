package snownee.lightingwand.compat;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.IExtendableRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import snownee.lightingwand.LW;
import snownee.lightingwand.common.RepairRecipe;

@JeiPlugin
public class JEICompat implements IModPlugin {

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(LW.MODID, "main");
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		IExtendableRecipeCategory<CraftingRecipe, ICraftingCategoryExtension> craftingCategory = registration.getCraftingCategory();
		craftingCategory.addCategoryExtension(RepairRecipe.class, RepairRecipeWrapper::new);
	}

	private static class RepairRecipeWrapper implements ICraftingCategoryExtension {
		private RepairRecipe recipe;

		public RepairRecipeWrapper(RepairRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void setRecipe(IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
			builder.setShapeless();
			ItemStack broken = new ItemStack(recipe.getRepairable());
			int duration = broken.getMaxDamage();
			broken.setDamageValue(duration);
			craftingGridHelper.setInputs(builder, VanillaTypes.ITEM_STACK, List.of(List.of(broken), List.of(recipe.getMaterial().getItems())), 0, 0);
			ItemStack output = new ItemStack(recipe.getRepairable());
			output.setDamageValue(Mth.clamp(duration - Mth.ceil(duration / recipe.getRatio()), 0, duration));
			craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM_STACK, List.of(output));
		}
	}
}