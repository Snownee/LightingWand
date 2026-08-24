package snownee.lightingwand.compat.lambdynlights;

import java.util.concurrent.CompletableFuture;

import dev.lambdaurora.lambdynlights.api.data.EntityLightSourceDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import snownee.lightingwand.CoreModule;

public class EntityLightData extends EntityLightSourceDataProvider {
	public EntityLightData(
			PackOutput packOutput,
			CompletableFuture<HolderLookup.Provider> registryProvider,
			String defaultNamespace) {
		super(packOutput, registryProvider, defaultNamespace);
	}

	@Override
	protected void generate(Context context) {
		context.add(CoreModule.PROJECTILE.get(), LWEntityLuminance.INSTANCE);
	}
}
