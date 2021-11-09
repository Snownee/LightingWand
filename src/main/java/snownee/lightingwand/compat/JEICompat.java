package snownee.lightingwand.compat;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IExtendableRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
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
		public void setIngredients(IIngredients ingredients) {
			List<Ingredient> inputs = Lists.newArrayListWithCapacity(2);
			ItemStack broken = new ItemStack(recipe.getRepairable());
			int duration = broken.getMaxDamage();
			broken.setDamageValue(duration);
			inputs.add(Ingredient.of(broken));
			inputs.add(recipe.getMaterial());
			ingredients.setInputIngredients(inputs);
			ItemStack output = new ItemStack(recipe.getRepairable());
			output.setDamageValue(Mth.clamp(duration - Mth.ceil(duration / recipe.getRatio()), 0, duration));
			ingredients.setOutput(VanillaTypes.ITEM, output);
		}
	}
}
