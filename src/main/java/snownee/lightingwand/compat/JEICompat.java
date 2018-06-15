package snownee.lightingwand.compat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import snownee.lightingwand.Config;
import snownee.lightingwand.common.ModConstants;

@JEIPlugin
public class JEICompat implements IModPlugin
{
    @Override
    public void register(IModRegistry registry)
    {
        if (Config.registerWand
                && Config.config.getBoolean("repairRecipe", Config.catGeneral, true, "Should use glowstone dust to repair wand"))
        {
            registry.addRecipes(getRecipe(registry.getJeiHelpers()), VanillaRecipeCategoryUid.CRAFTING);
        }
    }

    private Collection<WandRepairRecipeWrapper> getRecipe(IJeiHelpers JeiHelpers)
    {
        List<WandRepairRecipeWrapper> list = new ArrayList<>();
        list.add(new WandRepairRecipeWrapper(JeiHelpers));
        return list;
    }

    private static class WandRepairRecipeWrapper implements ICraftingRecipeWrapper
    {
        private final IJeiHelpers helpers;

        public WandRepairRecipeWrapper(IJeiHelpers jeiHelpers)
        {
            helpers = jeiHelpers;
        }

        @Override
        public void getIngredients(IIngredients ingredients)
        {
            List<List<ItemStack>> inputs = new ArrayList<>();
            ItemStack brokenWand = new ItemStack(ModConstants.WAND);
            int duration = brokenWand.getMaxItemUseDuration();
            brokenWand.setItemDamage(duration);
            inputs.add(helpers.getStackHelper().toItemStackList(brokenWand));
            inputs.add(helpers.getStackHelper().toItemStackList("dustGlowstone"));
            ingredients.setInputLists(ItemStack.class, inputs);
            ingredients.setOutput(ItemStack.class, new ItemStack(ModConstants.WAND, 1, MathHelper.clamp(duration
                    - MathHelper.ceil(duration / 4F), 0, duration)));
        }
    }
}
