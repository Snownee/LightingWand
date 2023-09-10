package snownee.lightingwand.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import snownee.kiwi.Mod;
import snownee.kiwi.loader.Platform;
import snownee.lightingwand.LW;
import snownee.lightingwand.LightEntity;
import snownee.lightingwand.compat.TREnergyCompat;
import snownee.lightingwand.fabric.RepairRecipeCondition;

@Mod(LW.ID)
public class CommonProxy implements ModInitializer {
	public static final boolean shimmerCompat = Platform.isModLoaded("shimmer");

	public static void postRegister() {
		if (Platform.isModLoaded("team_reborn_energy"))
			TREnergyCompat.init();
		if (Platform.isPhysicalClient())
			ClientProxy.postRegister();
	}

	public static Packet<ClientGamePacketListener> getAddEntityPacket(LightEntity entity) {
		Entity owner = entity.getOwner();
		return new ClientboundAddEntityPacket(entity, owner == null ? 0 : owner.getId());
	}

	@Override
	public void onInitialize() {
		ResourceConditions.register(RepairRecipeCondition.ID, RepairRecipeCondition.INSTANCE);
	}
}
