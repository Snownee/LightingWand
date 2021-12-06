package snownee.lightingwand;

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

}
