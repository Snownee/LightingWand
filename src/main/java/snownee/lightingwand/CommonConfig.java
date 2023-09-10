package snownee.lightingwand;

import org.joml.Vector3f;

import snownee.kiwi.config.KiwiConfig;
import snownee.kiwi.config.KiwiConfig.GameRestart;
import snownee.kiwi.config.KiwiConfig.Range;

@KiwiConfig
public class CommonConfig {

	@GameRestart
	public static boolean shootProjectile = true;

	@Range(min = 0)
	public static int energyPerUse = 200;

	public static boolean repairRecipe = true;

	@GameRestart
	@Range(min = 1)
	public static int wandDurability = 255;

	public static int defaultLightColor = 0xFFFFFD55;
	public static Vector3f defaultLightColorVector = new Vector3f();

	static {
		updateColorVector(null);
	}
	
	@KiwiConfig.Listen("defaultLightColor")
	public static void updateColorVector(String path) {
		LW.LOGGER.debug("Updating default light color vector");
		defaultLightColorVector = intColorToVector3(defaultLightColor);
	}

	public static Vector3f intColorToVector3(int color) {
		float r = ((color >> 16) & 255) / 255F;
		float g = ((color >> 8) & 255) / 255F;
		float b = (color & 255) / 255F;
		return new Vector3f(r, g, b);
	}
}
