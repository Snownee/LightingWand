package snownee.lightingwand.proxy;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import snownee.lightingwand.common.EntityLight;
import snownee.lightingwand.common.ItemWand;
import snownee.lightingwand.common.ModConstants;

public class CommonProxy
{
    @OverridingMethodsMustInvokeSuper
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @OverridingMethodsMustInvokeSuper
    public void init(FMLInitializationEvent event)
    {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModConstants.WAND, new IBehaviorDispenseItem()
        {
            @Override
            public ItemStack dispense(IBlockSource source, ItemStack stack)
            {
                if (ItemWand.isUsable(stack))
                {
                    World world = source.getWorld();
                    IPosition iposition = BlockDispenser.getDispensePosition(source);
                    EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
                    EntityLight entity = new EntityLight(world, iposition.getX(), iposition.getY(), iposition.getZ());
                    entity.shoot(enumfacing.getFrontOffsetX(), enumfacing.getFrontOffsetY()
                            + 0.1F, enumfacing.getFrontOffsetZ(), 1.3F + world.rand.nextFloat() * 0.4F, 0);
                    entity.motionX += world.rand.nextGaussian() * 0.1D;
                    entity.motionZ += world.rand.nextGaussian() * 0.1D;
                    world.spawnEntity(entity);
                    stack.attemptDamageItem(1, world.rand, null);
                }
                return stack;
            }
        });
    }

    @OverridingMethodsMustInvokeSuper
    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
