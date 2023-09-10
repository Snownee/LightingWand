package snownee.lightingwand.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import snownee.lightingwand.CommonConfig;

public class EnergyRepair implements IEnergyStorage {
	protected ItemStack container;

	public EnergyRepair(ItemStack container) {
		this.container = container;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (container.isEmpty() || !container.isDamaged() || maxReceive < CommonConfig.energyPerUse) {
			return 0;
		}
		if (!simulate) {
			container.setDamageValue(container.getDamageValue() - 1);
		}
		return CommonConfig.energyPerUse;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return Math.max((container.getMaxDamage() - container.getDamageValue()) * CommonConfig.energyPerUse, 0);
	}

	@Override
	public int getMaxEnergyStored() {
		return container.getMaxDamage() * CommonConfig.energyPerUse;
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}

}
