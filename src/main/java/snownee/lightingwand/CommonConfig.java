package snownee.lightingwand;

import org.joml.Vector3f;

import snownee.kiwi.config.ConfigUI;
import snownee.kiwi.config.KiwiConfig;
import snownee.kiwi.config.KiwiConfig.Comment;
import snownee.kiwi.config.KiwiConfig.Range;

@KiwiConfig
public class CommonConfig {

	@Comment("Should wand be able to shoot projectile.")
	public static boolean shootProjectile = true;

	@Comment("How much FE to repair one use of wand. Zero to disable.")
	@Range(min = 0)
	public static int energyPerUse = 50;

	@Comment("Should use glowstone dust to repair wand.")
	public static boolean repairRecipe = true;

	@Comment("Max durability of wand.")
	@Range(min = 1)
	public static int wandDurability = 255;

	@ConfigUI.Color
	public static int defaultLightColor = 0xFFFD55;
	private static Vector3f defaultLightColorVector = new Vector3f();

	static {
		onChanged("defaultLightColor");
	}

	public static void onChanged(String path) {
		if ("defaultLightColor".equals(path)) {
			defaultLightColorVector = intColorToVector3(defaultLightColorVector, defaultLightColor);
		}
	}

	public static Vector3f intColorToVector3(Vector3f vector, int color) {
		vector.x = ((color >> 16) & 255) / 255F;
		vector.y = ((color >> 8) & 255) / 255F;
		vector.z = (color & 255) / 255F;
		return vector;
	}

	public static Vector3f getDefaultLightColor() {
		return defaultLightColorVector;
	}

}
