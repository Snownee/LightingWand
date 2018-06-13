package snownee.lightingwand.common;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import snownee.lightingwand.LW;

@GameRegistry.ObjectHolder(LW.MODID)
public class ModConstants
{
    @GameRegistry.ObjectHolder("light")
    public static final Block LIGHT = Blocks.AIR;

    @GameRegistry.ObjectHolder("wand")
    public static final Item WAND = Items.AIR;
}
