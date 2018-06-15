package snownee.lightingwand;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = LW.MODID)
public class Config
{
    public static Configuration config;
    public static boolean registerWand;
    public static boolean shootProjectile;
    public static int energyPerUse;
    public static String catGeneral = "General";

    public static void preInit(File file)
    {
        config = new Configuration(file, true);
        config.load();
        config.addCustomCategoryComment(catGeneral, "Basic features of Lighting Wand.");
        registerWand = config.getBoolean("registerWand", catGeneral, true, "Enable lighting wand item.");
        shootProjectile = config.getBoolean("shootProjectile", catGeneral, true, "Should wand shoot projectile.");
        energyPerUse = config.getInt("energyPerUse", catGeneral, 50, 0, Integer.MAX_VALUE, "How much FE to repair one use of wand. Zero to disable.");
    }

    public static void postInit()
    {
        if (config.hasChanged())
        {
            config.save();
        }
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(LW.MODID))
        {
            config.save();
        }
    }
}
