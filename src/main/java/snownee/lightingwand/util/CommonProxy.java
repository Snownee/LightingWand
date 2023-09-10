package snownee.lightingwand.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import snownee.kiwi.Mod;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.loader.event.InitEvent;
import snownee.lightingwand.LW;
import snownee.lightingwand.compat.TREnergyCompat;
import snownee.lightingwand.fabric.RepairRecipeCondition;

@Mod(LW.ID)
public class CommonProxy implements ModInitializer {
	public static void postRegister(InitEvent event) {
		if (Platform.isModLoaded("team_reborn_energy"))
			TREnergyCompat.init();
	}

	@Override
	public void onInitialize() {
		ResourceConditions.register(RepairRecipeCondition.ID, RepairRecipeCondition.INSTANCE);
	}
}
