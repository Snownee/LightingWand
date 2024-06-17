package snownee.lightingwand.compat;

import java.util.List;

import com.google.common.collect.Lists;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapelessDisplay;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import snownee.lightingwand.RepairRecipe;

public class REICompat implements REIClientPlugin {

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		registry.registerRecipeFiller(RepairRecipe.class, RecipeType.CRAFTING, RepairRecipeWrapper::new);
	}

	private static class RepairRecipeWrapper extends DefaultCustomShapelessDisplay {
		public RepairRecipeWrapper(RecipeHolder<RepairRecipe> holder) {
			super(
					holder,
					EntryIngredients.ofIngredients(getIngredients(holder.value())),
					List.of(EntryIngredients.of(getOutput(holder.value()))));
		}

		private static List<Ingredient> getIngredients(RepairRecipe recipe) {
			List<Ingredient> inputs = Lists.newArrayListWithCapacity(2);
			ItemStack broken = new ItemStack(recipe.repairable());
			int duration = broken.getMaxDamage();
			broken.setDamageValue(duration);
			inputs.add(Ingredient.of(broken));
			inputs.add(recipe.material());
			return inputs;
		}

		private static ItemStack getOutput(RepairRecipe recipe) {
			ItemStack broken = new ItemStack(recipe.repairable());
			int duration = broken.getMaxDamage();
			broken.setDamageValue(Mth.clamp(duration - Mth.ceil(duration / recipe.ratio()), 0, duration));
			return broken;
		}
	}
}
