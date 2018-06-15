package snownee.lightingwand.common;

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
    private static final int ORE = OreDictionary.getOreID("dustGlowstone");

    public RecipeRepair()
    {
        super();
        setRegistryName(LW.MODID, "repair");
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        int dust = 0;
        ItemStack wand = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
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
            else if (!itemstack.isEmpty() && isGlowstoneDust(itemstack))
            {
                dust++;
            }
            else if (itemstack != ItemStack.EMPTY)
            {
                return false;
            }
        }
        return !wand.isEmpty() && dust > 0 && wand.getItemDamage() - MathHelper.ceil(wand.getMaxItemUseDuration() / 4F)
                * dust > -MathHelper.ceil(wand.getMaxItemUseDuration() / 4F);
    }

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
            else if (!itemstack.isEmpty() && isGlowstoneDust(itemstack))
            {
                int count = itemstack.getCount();
                if (count > 0)
                {
                    dust++;
                }
            }
        }
        int damage = MathHelper.clamp(wand.getItemDamage()
                - MathHelper.ceil(wand.getMaxItemUseDuration() / 4F) * dust, 0, ModConstants.WAND.getMaxDamage(wand));
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

    public static boolean isGlowstoneDust(ItemStack stack)
    {
        for (int i : OreDictionary.getOreIDs(stack))
        {
            if (i == ORE)
            {
                return true;
            }
        }
        return false;
    }
}
