package snownee.lightingwand.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;


public final class LWDataGen {
	private LWDataGen() {
	}

	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(LWItemTags::new);
		event.createProvider(LWRecipes.Runner::new);
//		event.createProvider((output, lookupProvider) -> new KiwiLanguageProvider(output, LW.ID, lookupProvider));

//		event.createProvider((output, lookupProvider) -> new EntityLightData(output, lookupProvider, LW.ID));
//		event.createProvider((output, lookupProvider) -> new ItemLightData(output, lookupProvider, LW.ID));
	}
}
