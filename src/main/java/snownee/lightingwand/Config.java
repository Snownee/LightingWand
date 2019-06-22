package snownee.lightingwand;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = LW.MODID)
public class Config
{
    static final ForgeConfigSpec spec;

    static
    {
        final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
        spec = specPair.getRight();
    }

    public static BooleanValue shootProjectile;
    public static IntValue energyPerUse;
    public static BooleanValue repairRecipe;
    public static IntValue wandDurability;

    private Config(ForgeConfigSpec.Builder builder)
    {
        shootProjectile = builder.comment("Should wand be able to shoot projectile.").define("shootProjectile", true);
        energyPerUse = builder.comment("How much FE to repair one use of wand. Zero to disable.").defineInRange("energyPerUse", 50, 0, Integer.MAX_VALUE - 1);
        repairRecipe = builder.comment("Should use glowstone dust to repair wand.").define("repairRecipe", true);
        wandDurability = builder.comment("Max durability of wand.").defineInRange("wandDurability", 255, 1, Integer.MAX_VALUE - 1);
    }
}
