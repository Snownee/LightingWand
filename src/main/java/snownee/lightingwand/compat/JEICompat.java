package snownee.lightingwand.compat;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IExtendableRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import snownee.lightingwand.LW;
import snownee.lightingwand.common.RepairRecipe;

@JeiPlugin
public class JEICompat implements IModPlugin
{

    @Override
    public ResourceLocation getPluginUid()
    {
        return new ResourceLocation(LW.MODID, LW.MODID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        registration.addRecipes(RepairRecipe.Serializer.recipes, VanillaRecipeCategoryUid.CRAFTING);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration)
    {
        IExtendableRecipeCategory<ICraftingRecipe, ICraftingCategoryExtension> craftingCategory = registration.getCraftingCategory();
        craftingCategory.addCategoryExtension(RepairRecipe.class, RepairRecipeWrapper::new);
    }

    private static class RepairRecipeWrapper implements ICraftingCategoryExtension
    {
        private RepairRecipe recipe;

        public RepairRecipeWrapper(RepairRecipe recipe)
        {
            this.recipe = recipe;
        }

        @Override
        public void setIngredients(IIngredients ingredients)
        {
            List<Ingredient> inputs = Lists.newArrayListWithCapacity(2);
            ItemStack broken = new ItemStack(recipe.getRepairable());
            int duration = broken.getMaxDamage();
            broken.setDamage(duration);
            inputs.add(Ingredient.fromStacks(broken));
            inputs.add(recipe.getMaterial());
            ingredients.setInputIngredients(inputs);
            ItemStack output = new ItemStack(recipe.getRepairable());
            output.setDamage(MathHelper.clamp(duration - MathHelper.ceil(duration / recipe.getRatio()), 0, duration));
            ingredients.setOutput(VanillaTypes.ITEM, output);
        }
    }
}
