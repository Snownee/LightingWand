package snownee.lightingwand.compat;

import org.joml.Vector3f;

import com.lowdragmc.shimmer.client.light.ColorPointLight;
import com.lowdragmc.shimmer.client.light.LightManager;
import com.lowdragmc.shimmer.event.ShimmerReloadEvent;
import com.lowdragmc.shimmer.forge.event.ForgeShimmerReloadEvent;

import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import snownee.lightingwand.ColoredLightBlockEntity;
import snownee.lightingwand.CommonConfig;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.LightBlock;
import snownee.lightingwand.LightEntity;
import snownee.lightingwand.WandItem;

public interface ShimmerCompat {

	static void init() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((ForgeShimmerReloadEvent event) -> {
			if (event.event.getReloadType() == ShimmerReloadEvent.ReloadType.COLORED_LIGHT) {
				LightManager.INSTANCE.registerItemLight(CoreModule.WAND.get(), stack -> {
					float range = WandItem.getLightValue(stack) / 2F;
					if (stack.isDamaged()) {
						range *= 0.1F + 0.9F - 0.9F * stack.getDamageValue() / stack.getMaxDamage();
					}
					return new ColorPointLight.Template(range, CoreModule.WAND.get().getColor(stack));
				});
			}
		});
	}

	static void addLight(LightEntity lightEntity) {
		lightEntity.shimmerLight = LightManager.INSTANCE.addLight(lightEntity.position().toVector3f(), 0, 1);
	}

	static void removeLight(LightEntity lightEntity) {
		((ColorPointLight) lightEntity.shimmerLight).remove();
		lightEntity.shimmerLight = null;
	}

	static void updateLight(LightEntity lightEntity) {
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

	static void addLight(ColoredLightBlockEntity lightEntity) {
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
