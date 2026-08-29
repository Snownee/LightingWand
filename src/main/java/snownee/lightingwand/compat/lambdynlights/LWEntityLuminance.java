package snownee.lightingwand.compat.lambdynlights;

import org.jetbrains.annotations.Range;

import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.Entity;
import snownee.lightingwand.LightEntity;

public class LWEntityLuminance implements EntityLuminance {
	public static final LWEntityLuminance INSTANCE = new LWEntityLuminance();

	@Override
	public Type type() {
		return LambDynLightsCompat.ENTITY;
	}

	@Override
	public @Range(from = 0L, to = 15L) int getLuminance(ItemLightSourceManager itemLightSourceManager, Entity entity) {
		if (!(entity instanceof LightEntity light)) {
			return 0;
		}
		return light.getLightValue();
	}
}
