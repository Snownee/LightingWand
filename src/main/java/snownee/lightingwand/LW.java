package snownee.lightingwand;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import snownee.lightingwand.compat.PsiCompat;

@Mod(modid = LW.MODID, name = LW.NAME, version = LW.VERSION, guiFactory = LW.GUI_FACTORY, acceptedMinecraftVersions = "[1.12.2,1.13)", dependencies = "required-after:forge@[14.23.3.2698,);")
public class LW
{
    public static final String MODID = "lightingwand";
    public static final String NAME = "Lighting Wand";
    public static final String VERSION = "@VERSION_INJECT@";
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
        if (Config.psiCompat && Loader.isModLoaded("psi"))
        {
            PsiCompat.init();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        Config.postInit();
    }
}
