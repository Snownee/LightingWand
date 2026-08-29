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

	@GameRestart
	@Range(min = 1)
	public static int wandDurability = 255;

	public static int defaultLightColor = 0xFFFFFD55;
}
