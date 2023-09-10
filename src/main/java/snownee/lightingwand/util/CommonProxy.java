package snownee.lightingwand.util;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import snownee.kiwi.loader.Platform;
import snownee.lightingwand.LW;
import snownee.lightingwand.LightEntity;
import snownee.lightingwand.forge.RepairRecipeCondition;

@Mod(LW.ID)
public class CommonProxy {
	public static final boolean shimmerCompat = Platform.isModLoaded("shimmer");

	public CommonProxy() {
		if (Platform.isPhysicalClient())
			ClientProxy.init();
	}

	public static void postRegister() {
		CraftingHelper.register(new RepairRecipeCondition.Serializer());
		if (Platform.isPhysicalClient())
			ClientProxy.postRegister();
	}

	public static Packet<ClientGamePacketListener> getAddEntityPacket(LightEntity entity) {
		return NetworkHooks.getEntitySpawningPacket(entity);
	}
}
