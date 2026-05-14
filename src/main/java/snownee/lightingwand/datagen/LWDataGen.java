package snownee.lightingwand.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;


public final class LWDataGen {
	private LWDataGen() {
	}

	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(LWItemTagProvider::new);
		event.createProvider(LWRecipeProvider.Runner::new);
	}
}
