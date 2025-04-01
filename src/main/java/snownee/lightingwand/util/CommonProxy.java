package snownee.lightingwand.util;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import snownee.kiwi.loader.Platform;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.LW;
import snownee.lightingwand.LightEntity;
import snownee.lightingwand.neoforge.EnergyRepair;
import snownee.lightingwand.neoforge.RepairRecipeCondition;

@Mod(LW.ID)
public class CommonProxy {
	public static final boolean shimmerCompat = Platform.isModLoaded("shimmer");
	public static final ItemCapability<EnergyRepair, @Nullable Void> ENERGY_REPAIR_CAPABILITY =
			ItemCapability.createVoid(LW.id("energy_repair"), EnergyRepair.class);

	public static void postRegister() {
		if (Platform.isPhysicalClient()) {
			ClientProxy.postRegister();
		}
	}

	public static Packet<ClientGamePacketListener> getAddEntityPacket(LightEntity entity, ServerEntity serverEntity) {
		Entity owner = entity.getOwner();
		return new ClientboundAddEntityPacket(entity, serverEntity, owner == null ? 0 : owner.getId());
	}

	public CommonProxy() {
		NeoForge.EVENT_BUS.addListener((RegisterEvent event) -> event.register(
				NeoForgeRegistries.Keys.CONDITION_CODECS,
				LW.id("repair_recipe"),
				() -> RepairRecipeCondition.CODEC));

		NeoForge.EVENT_BUS.addListener((RegisterCapabilitiesEvent event) -> event.registerItem(
				ENERGY_REPAIR_CAPABILITY,
				(stack, context) -> new EnergyRepair(stack),
				CoreModule.WAND.get()));
	}
}
