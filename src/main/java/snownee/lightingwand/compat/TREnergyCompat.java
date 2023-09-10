package snownee.lightingwand.compat;

import snownee.lightingwand.CoreModule;
import snownee.lightingwand.fabric.EnergyRepair;
import team.reborn.energy.api.EnergyStorage;

public interface TREnergyCompat {
	static void init() {
		EnergyStorage.ITEM.registerForItems((stack, ctx) -> new EnergyRepair(ctx), CoreModule.WAND.get());
	}
}
