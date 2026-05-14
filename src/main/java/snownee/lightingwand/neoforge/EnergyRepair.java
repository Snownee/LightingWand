package snownee.lightingwand.neoforge;

import org.jspecify.annotations.NonNull;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import snownee.lightingwand.CommonConfig;

public class EnergyRepair implements EnergyHandler {
	protected ItemStack container;

	public EnergyRepair(ItemStack container) {
		this.container = container;
	}

	@Override
	public long getAmountAsLong() {
		return Math.max((container.getMaxDamage() - container.getDamageValue()) * CommonConfig.energyPerUse, 0);
	}

	@Override
	public long getCapacityAsLong() {
		return (long) container.getMaxDamage() * CommonConfig.energyPerUse;
	}

	@Override
	public int insert(int amount, @NonNull TransactionContext transaction) {
		if (container.isEmpty() || !container.isDamaged() || amount < CommonConfig.energyPerUse) {
			return 0;
		}
		return CommonConfig.energyPerUse;
	}

	@Override
	public int extract(int amount, TransactionContext transaction) {
		return 0;
	}
}

