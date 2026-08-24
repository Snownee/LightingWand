package snownee.lightingwand.compat.lambdynlights;

import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import snownee.lightingwand.LW;

public class LambDynLightsCompat implements DynamicLightsInitializer {
	public static final EntityLuminance.Type ENTITY = EntityLuminance.Type.registerSimple(LW.id("entity"), LWEntityLuminance.INSTANCE);

	@Override
	public void onInitializeDynamicLights(DynamicLightsContext context) {
	}
}
