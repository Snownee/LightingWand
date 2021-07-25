package snownee.lightingwand.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ObjectHolder;
import snownee.lightingwand.LW;

@ObjectHolder(LW.MODID)
public class ModConstants {
    @ObjectHolder("light")
    public static final Block LIGHT = Blocks.AIR;

    @ObjectHolder("light")
    public static final EntityType<? extends ThrowableProjectile> LIGHT_ENTITY_TYPE = null;

    @ObjectHolder("wand")
    public static final Item WAND = Items.AIR;

    @ObjectHolder("repair")
    public static final RecipeSerializer<?> REPAIR = null;
}
