package snownee.lightingwand.util;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import snownee.kiwi.Mod;
import snownee.kiwi.loader.Platform;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.LW;
import snownee.lightingwand.compat.TREnergyCompat;

@Mod(LW.ID)
public class CommonProxy implements ModInitializer {
	public static final boolean shimmerCompat = false; // Platform.isModLoaded("shimmer");

	public static void postRegister() {
		if (Platform.isModLoaded("team_reborn_energy")) {
			TREnergyCompat.init();
		}
		if (Platform.isPhysicalClient()) {
			ClientProxy.postRegister();
		}
	}

	@Override
	public void onInitialize() {
		RecipeSynchronization.synchronizeRecipeSerializer(CoreModule.REPAIR.getOrCreate());
	}
}
