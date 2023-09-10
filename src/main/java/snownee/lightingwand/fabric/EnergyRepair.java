package snownee.lightingwand.fabric;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import snownee.lightingwand.CommonConfig;
import team.reborn.energy.api.EnergyStorage;

public class EnergyRepair implements EnergyStorage {
	private final ContainerItemContext ctx;

	public EnergyRepair(ContainerItemContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		ItemStack container = ctx.getItemVariant().toStack();
		if (container.isEmpty() || !container.isDamaged() || maxAmount < CommonConfig.energyPerUse) {
			return 0;
		}
		container.setDamageValue(container.getDamageValue() - 1);
		ItemVariant newVariant = ItemVariant.of(container);
		try (Transaction nested = transaction.openNested()) {
			if (ctx.extract(ctx.getItemVariant(), 1, nested) == 1 && ctx.insert(newVariant, 1, nested) == 1) {
				nested.commit();
				return CommonConfig.energyPerUse;
			}
		}
		return 0;
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public long getAmount() {
		ItemStack container = ctx.getItemVariant().toStack();
		return Math.max((container.getMaxDamage() - container.getDamageValue()) * CommonConfig.energyPerUse, 0);
	}

	@Override
	public long getCapacity() {
		ItemStack container = ctx.getItemVariant().toStack();
		return container.getMaxDamage() * CommonConfig.energyPerUse;
	}

	@Override
	public boolean supportsExtraction() {
		return false;
	}
}

