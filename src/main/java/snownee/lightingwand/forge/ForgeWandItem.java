package snownee.lightingwand.forge;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import snownee.lightingwand.CommonConfig;
import snownee.lightingwand.WandItem;

public class ForgeWandItem extends WandItem {
	public ForgeWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
		return new ICapabilityProvider() {
			private final LazyOptional<EnergyRepair> handler = LazyOptional.of(() -> new EnergyRepair(stack));

			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
				if (cap == ForgeCapabilities.ENERGY && CommonConfig.energyPerUse > 0) {
					return handler.cast();
				}
				return LazyOptional.empty();
			}
		};
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}
}
