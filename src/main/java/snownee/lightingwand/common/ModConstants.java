package snownee.lightingwand.common;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.registries.ObjectHolder;
import snownee.lightingwand.LW;

@ObjectHolder(LW.MODID)
public class ModConstants {
	@ObjectHolder("light")
	public static final Block LIGHT = Blocks.AIR;

	@ObjectHolder("light")
	public static final EntityType<? extends ThrowableEntity> LIGHT_ENTITY_TYPE = null;

	@ObjectHolder("wand")
	public static final Item WAND = Items.AIR;

	@ObjectHolder("repair")
	public static final IRecipeSerializer<?> REPAIR = null;
}
