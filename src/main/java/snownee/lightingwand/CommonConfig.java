package snownee.lightingwand;

import com.mojang.math.Vector3f;

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

	public static int defaultLightColor = 0xFFFFFD55;
	public static Vector3f defaultLightColorVector = Vector3f.ZERO;

	static {
		onChanged("defaultLightColor");
	}

	public static void onChanged(String path) {
		if ("defaultLightColor".equals(path)) {
			defaultLightColorVector = intColorToVector3(defaultLightColor);
		}
	}

	public static Vector3f intColorToVector3(int color) {
		float r = ((color >> 16) & 255) / 255F;
		float g = ((color >> 8) & 255) / 255F;
		float b = (color & 255) / 255F;
		return new Vector3f(r, g, b);
	}

}
