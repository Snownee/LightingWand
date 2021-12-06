package snownee.lightingwand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;

@Mod(LW.MODID)
public class LW {
	public static final String MODID = "lightingwand";
	public static final String NAME = "Lighting Wand";
	public static final Logger logger = LogManager.getLogger(LW.NAME);
}
