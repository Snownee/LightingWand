package snownee.lightingwand;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LW.MODID, name = LW.NAME, version = LW.VERSION, guiFactory = LW.GUI_FACTORY)
public class LW
{
    public static final String MODID = "lightingwand";
    public static final String NAME = "Lighting Wand";
    public static final String VERSION = "1.0.0";
    public static final String GUI_FACTORY = "snownee.lightingwand.common.GuiFactory";

    private static final LW INSTANCE = new LW();

    @Mod.InstanceFactory
    public static LW getInstance()
    {
        return INSTANCE;
    }

    public static Logger logger;

    public static void log(Object o)
    {
        logger.info(o);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        Config.preInit(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        Config.postInit();
    }
}
