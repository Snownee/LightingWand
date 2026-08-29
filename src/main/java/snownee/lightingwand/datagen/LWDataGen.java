package snownee.lightingwand.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import snownee.kiwi.datagen.KiwiLanguageProvider;
import snownee.lightingwand.LW;
import snownee.lightingwand.compat.lambdynlights.EntityLightData;
import snownee.lightingwand.compat.lambdynlights.ItemLightData;


public final class LWDataGen {
	private LWDataGen() {
	}

	public static void gatherData(GatherDataEvent.Client event) {
		event.createProvider(LWItemTagProvider::new);
		event.createProvider(LWRecipeProvider.Runner::new);
//		event.createProvider((output, lookupProvider) -> new KiwiLanguageProvider(output, LW.ID, lookupProvider));

//		event.createProvider((output, lookupProvider) -> new EntityLightData(output, lookupProvider, LW.ID));
//		event.createProvider((output, lookupProvider) -> new ItemLightData(output, lookupProvider, LW.ID));
	}
}
