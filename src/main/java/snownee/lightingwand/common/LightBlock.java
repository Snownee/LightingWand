package snownee.lightingwand.common;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class LightBlock extends Block implements IWaterLoggable
{
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", 1, 15);

    public LightBlock()
    {
        super(Block.Properties.create(Material.AIR).lightValue(15).sound(SoundType.SLIME));
        setDefaultState(stateContainer.getBaseState().with(WATERLOGGED, false).with(LIGHT, 15));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public int getLightValue(BlockState state) {
        return state.get(LIGHT);
    }

    @Override
    public boolean isAir(BlockState state)
    {
        return true;
    }

    @Override
    public boolean isAir(BlockState state, IBlockReader world, BlockPos pos)
    {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_)
    {
        return (EffectiveSide.get() == LogicalSide.CLIENT && hasItem()) ? VoxelShapes.fullCube() : VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_)
    {
        return VoxelShapes.empty();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (hasItem())
        {
            float x = pos.getX() + 0.3F + rand.nextFloat() * 0.4F;
            float y = pos.getY() + 0.5F;
            float z = pos.getZ() + 0.3F + rand.nextFloat() * 0.4F;

            worldIn.addParticle(new RedstoneParticleData(1, 1, 0, 1.0F), x, y, z, 0, 0, 0);
        }
        super.animateTick(stateIn, worldIn, pos, rand);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasItem()
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null)
        {
            return false;
        }
        Item main = player.getHeldItemMainhand().getItem();
        Item off = player.getHeldItemOffhand().getItem();
        return main == ModConstants.WAND || off == ModConstants.WAND;
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder)
    {
        builder.add(WATERLOGGED, LIGHT);
    }

    @Override
    public IFluidState getFluidState(BlockState state)
    {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        IBlockReader iblockreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
        return super.getStateForPlacement(context).with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
    }
}
