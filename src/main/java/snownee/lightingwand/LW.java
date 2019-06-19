package snownee.lightingwand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(LW.MODID)
public class LW
{
    public static final String MODID = "lightingwand";
    public static final String NAME = "Lighting Wand";

    public static Logger logger = LogManager.getLogger(LW.NAME);

    public LW()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.spec, MODID + ".toml");
    }
}
