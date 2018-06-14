package snownee.lightingwand.common;

import java.util.Arrays;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import snownee.lightingwand.LW;

public class RecipeRepair extends Impl<IRecipe> implements IRecipe
{
    int ore;

    public RecipeRepair()
    {
        super();
        setRegistryName(LW.MODID, "repair");
        ore = OreDictionary.getOreID("dustGlowstone");
    }

    @SuppressWarnings("unlikely-arg-type")
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        int dust = 0;
        ItemStack wand = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty())
            {
                for (int id : OreDictionary.getOreIDs(itemstack))
                {
                    LW.log(OreDictionary.getOreName(id));
                }
            }
            if (itemstack.getItem() == ModConstants.WAND && itemstack.getItemDamage() != 0)
            {
                if (wand.isEmpty())
                {
                    wand = itemstack;
                }
                else
                {
                    return false;
                }
            }
            else if (!itemstack.isEmpty() && Arrays.asList(OreDictionary.getOreIDs(itemstack)).contains(ore))
            {
                dust++;
            }
            else if (itemstack != ItemStack.EMPTY)
            {
                return false;
            }
        }
        return !wand.isEmpty() && dust > 0
                && wand.getItemDamage() - wand.getMaxItemUseDuration() / 4 * dust > -wand.getMaxItemUseDuration() / 4;
    }

    @SuppressWarnings("unlikely-arg-type")
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        int dust = 0;
        ItemStack wand = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack.getItem() == ModConstants.WAND)
            {
                wand = itemstack;
            }
            else if (!itemstack.isEmpty()
                    && Arrays.asList(OreDictionary.getOreIDs(itemstack)).contains(OreDictionary.getOreID("dustGlowstone")))
            {
                int count = itemstack.getCount();
                if (count > 0)
                {
                    dust++;
                }
            }
        }
        int damage = MathHelper.clamp(wand.getItemDamage()
                - wand.getMaxItemUseDuration() / 4 * dust, 0, ModConstants.WAND.getMaxDamage(wand));
        return new ItemStack(ModConstants.WAND, 1, damage, wand.getTagCompound());
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return width > 1 || height > 1;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }
}
