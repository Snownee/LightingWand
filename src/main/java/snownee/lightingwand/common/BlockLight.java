package snownee.lightingwand.common;

import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.lightingwand.Config;
import snownee.lightingwand.LW;

public class BlockLight extends Block
{

    private static final AxisAlignedBB NULL_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public BlockLight()
    {
        super(Material.PLANTS, MapColor.AIR);
        setRegistryName(LW.MODID, "light");
        setUnlocalizedName(LW.MODID + ".light");
        setLightLevel(1);
        setSoundType(SoundType.SLIME);
        disableStats();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return (CommonRegistry.isClient && hasItem()) ? FULL_BLOCK_AABB : NULL_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        return (hasItem() ? FULL_BLOCK_AABB : NULL_AABB).offset(pos);
    }

    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return true;
    }

    @Override
    public boolean isCollidable()
    {
        return false;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return null;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean causesSuffocation(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue(IBlockState state)
    {
        return 1.0F;
    }

    @Override
    public boolean isNormalCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state)
    {
        return false;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
    }

    @Override
    protected boolean canSilkHarvest()
    {
        return false;
    }

    @Override
    public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, Predicate<IBlockState> target)
    {
        return true;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state)
    {
        return EnumPushReaction.DESTROY;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (hasItem())
        {
            float x = pos.getX() + 0.3F + rand.nextFloat() * 0.4F;
            float y = pos.getY() + 0.5F;
            float z = pos.getZ() + 0.3F + rand.nextFloat() * 0.4F;

            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 1, 1, 0);
        }
        super.randomDisplayTick(stateIn, worldIn, pos, rand);
    }

    @SideOnly(Side.CLIENT)
    public static boolean hasItem()
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        Item main = player.getHeldItemMainhand().getItem();
        Item off = player.getHeldItemOffhand().getItem();
        if (Config.registerWand && (main == ModConstants.WAND || off == ModConstants.WAND))
        {
            return true;
        }
        if (Config.psiCompat && Loader.isModLoaded("psi"))
        {
            return main.getRegistryName().toString().equals("psi:cad")
                    || off.getRegistryName().toString().equals("psi:cad");
        }
        return false;
    }
}
