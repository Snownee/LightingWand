package snownee.lightingwand.compat;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import snownee.lightingwand.CommonConfig;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.common.ColoredLightBlockEntity;
import snownee.lightingwand.common.LightBlock;
import snownee.lightingwand.common.LightEntity;
import snownee.lightingwand.common.WandItem;

public class ShimmerCompat {

	public static void init() {
		LightManager.INSTANCE.registerItemLight(CoreModule.WAND.get(), stack -> {
			return new ColorPointLight.Template(WandItem.getLightValue(stack) / 2F, CoreModule.WAND.get().getColor(stack));
		});
	}

	public static void addLight(LightEntity lightEntity) {
		lightEntity.shimmerLight = LightManager.INSTANCE.addLight(new Vector3f(lightEntity.position()), 0, 1);
	}

	public static void removeLight(LightEntity lightEntity) {
		((ColorPointLight) lightEntity.shimmerLight).remove();
		lightEntity.shimmerLight = null;
	}

	public static void updateLight(LightEntity lightEntity) {
		ColorPointLight light = (ColorPointLight) lightEntity.shimmerLight;
		light.x = (float) lightEntity.position().x;
		light.y = (float) lightEntity.position().y;
		light.z = (float) lightEntity.position().z;
		int color = lightEntity.getColor();
		if (color == 0) {
			color = CommonConfig.defaultLightColor;
		}
		light.setColor(color);
		light.radius = lightEntity.getLightValue() / 2F;
		light.update();
	}

	public static void addLight(ColoredLightBlockEntity lightEntity) {
		int color = lightEntity.getColor();
		if (color == 0) {
			color = CommonConfig.defaultLightColor;
		}
		if (lightEntity.shimmerLight == null) {
			BlockPos pos = lightEntity.getBlockPos();
			lightEntity.shimmerLight = LightManager.INSTANCE.addLight(new Vector3f(pos.getX() + .5F, pos.getY() + .5F, pos.getZ() + .5F), color, lightEntity.getBlockState().getValue(LightBlock.LIGHT) / 2F);
		} else {
			ColorPointLight light = (ColorPointLight) lightEntity.shimmerLight;
			light.setColor(color);
			light.radius = lightEntity.getBlockState().getValue(LightBlock.LIGHT) / 2F;
			light.update();
		}
	}

	public static void removeLight(ColoredLightBlockEntity lightEntity) {
		((ColorPointLight) lightEntity.shimmerLight).remove();
		lightEntity.shimmerLight = null;
	}

}
