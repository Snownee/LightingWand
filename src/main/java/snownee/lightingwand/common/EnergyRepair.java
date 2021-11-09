package snownee.lightingwand.common;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import snownee.lightingwand.Config;

public class EnergyRepair implements IEnergyStorage {
	protected ItemStack container;

	public EnergyRepair(ItemStack container) {
		this.container = container;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (canReceive()) {
			if (!simulate && maxReceive >= Config.energyPerUse.get()) {
				container.setDamage(container.getDamage() - 1);
			}
			return maxReceive >= Config.energyPerUse.get() ? Config.energyPerUse.get() : 0;
		}
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return Math.max((container.getMaxDamage() - container.getDamage()) * Config.energyPerUse.get(), 0);
	}

	@Override
	public int getMaxEnergyStored() {
		return container.getMaxDamage() * Config.energyPerUse.get();
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return container.getDamage() > 0;
	}

}
