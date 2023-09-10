package snownee.lightingwand;

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

}
