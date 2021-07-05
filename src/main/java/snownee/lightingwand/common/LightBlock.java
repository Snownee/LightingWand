package snownee.lightingwand.common;

import java.util.Random;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import vazkii.psi.api.cad.ICAD;

public class LightBlock extends Block implements IWaterLoggable {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty LIGHT = IntegerProperty.create("light", 1, 15);

    public LightBlock() {
        super(AbstractBlock.Properties.from(Blocks.AIR).setLightLevel(state -> state.get(LIGHT)).sound(SoundType.SLIME));
        setDefaultState(stateContainer.getBaseState().with(WATERLOGGED, false).with(LIGHT, 15));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isAir(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return (FMLEnvironment.dist.isClient() && EffectiveSide.get() == LogicalSide.CLIENT && hasItem()) ? VoxelShapes.fullCube() : VoxelShapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_220071_1_, IBlockReader p_220071_2_, BlockPos p_220071_3_, ISelectionContext p_220071_4_) {
        return VoxelShapes.empty();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (hasItem()) {
            float x = pos.getX() + 0.3F + rand.nextFloat() * 0.4F;
            float y = pos.getY() + 0.5F;
            float z = pos.getZ() + 0.3F + rand.nextFloat() * 0.4F;

            worldIn.addParticle(new RedstoneParticleData(1, 1, 0, 1.0F), x, y, z, 0, 0, 0);
        }
        super.animateTick(stateIn, worldIn, pos, rand);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasItem() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        Item main = player.getHeldItemMainhand().getItem();
        Item off = player.getHeldItemOffhand().getItem();
        if (main == ModConstants.WAND || off == ModConstants.WAND) {
            return true;
        }
        if (CommonRegistry.psiCompat) {
            if (main instanceof ICAD || off instanceof ICAD) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void fillStateContainer(Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LIGHT);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        context.getWorld();
        context.getPos();
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        return super.getStateForPlacement(context).with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(handIn);
        if (stack.getItem() != ModConstants.WAND) {
            return ActionResultType.PASS;
        }
        int wandLight = WandItem.getLightValue(stack);
        int blockLight = state.get(LIGHT);
        if (wandLight != blockLight) {
            worldIn.setBlockState(pos, state.with(LIGHT, wandLight));
        } else {
            wandLight = wandLight % 15 + 1;
            stack.getOrCreateTag().putInt("Light", wandLight);
            worldIn.setBlockState(pos, state.with(LIGHT, wandLight));
        }
        return ActionResultType.SUCCESS;
    }
}
