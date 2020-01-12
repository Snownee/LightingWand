package snownee.lightingwand.common;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import snownee.lightingwand.Config;

public class EnergyRepair implements IEnergyStorage
{
    protected ItemStack container;

    public EnergyRepair(ItemStack container)
    {
        this.container = container;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        if (canReceive())
        {
            if (!simulate && maxReceive > Config.energyPerUse)
            {
                container.setItemDamage(container.getItemDamage() - 1);
            }
            return maxReceive > Config.energyPerUse ? Config.energyPerUse : 0;
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return 0;
    }

    @Override
    public int getEnergyStored()
    {
        return (container.getMaxDamage() - container.getItemDamage()) * Config.energyPerUse;
    }

    @Override
    public int getMaxEnergyStored()
    {
        return container.getMaxDamage() * Config.energyPerUse;
    }

    @Override
    public boolean canExtract()
    {
        return false;
    }

    @Override
    public boolean canReceive()
    {
        return container.getItemDamage() > 0;
    }

}
