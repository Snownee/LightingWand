package snownee.lightingwand.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import snownee.kiwi.datagen.KiwiLanguageProvider;
import snownee.lightingwand.LW;
import snownee.lightingwand.compat.lambdynlights.EntityLightData;
import snownee.lightingwand.compat.lambdynlights.ItemLightData;

public class LWDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(LWItemTagsProvider::new);
		pack.addProvider(LWBlockTagsProvider::new);
		pack.addProvider(LWRecipeProvider::new);
		pack.addProvider(KiwiLanguageProvider::new);

		pack.addProvider((output, lookupProvider) -> new EntityLightData(output, lookupProvider, LW.ID));
		pack.addProvider((output, lookupProvider) -> new ItemLightData(output, lookupProvider, LW.ID));
	}
}
