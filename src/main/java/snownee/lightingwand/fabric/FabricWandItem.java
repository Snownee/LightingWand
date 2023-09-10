package snownee.lightingwand.fabric;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import snownee.lightingwand.WandItem;

public class FabricWandItem extends WandItem implements FabricItem {
	public FabricWandItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
		return false;
	}
}
