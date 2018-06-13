package snownee.lightingwand;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import snownee.lightingwand.proxy.CommonProxy;

@Mod(modid = LW.MODID, name = LW.NAME, version = LW.VERSION)
public class LW
{
    public static final String MODID = "lightingwand";
    public static final String NAME = "Lighting Wand";
    public static final String VERSION = "1.0.0";

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

    @SidedProxy(clientSide = "snownee.lightingwand.proxy.ClientProxy", serverSide = "snownee.lightingwand.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }
}
