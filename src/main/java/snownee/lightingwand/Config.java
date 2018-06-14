package snownee.lightingwand;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

public class Config
{
    public static Configuration config;
    public static boolean registerWand;
    public static boolean repairRecipe;
    public static String catGeneral = "General";

    public static void init(File file)
    {
        config = new Configuration(file);
        config.load();
        config.addCustomCategoryComment(catGeneral, "Basic features of Lighting Wand");
        registerWand = config.getBoolean("registerWand", catGeneral, true, "Enable lighting wand item");
        repairRecipe = config.getBoolean("repairRecipe", catGeneral, true, "Should use glowstone dust to repair wand");
        if (config.hasChanged())
        {
            config.save();
        }
    }
}
