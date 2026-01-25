package snownee.lightingwand;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.kiwi.loader.Platform;
import snownee.lightingwand.util.ClientProxy;

public class LightBlock extends Block implements SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final IntegerProperty LIGHT = IntegerProperty.create("light", 1, 15);
	public static final MapCodec<LightBlock> CODEC = simpleCodec(LightBlock::new);

	public LightBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false).setValue(LIGHT, 15));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return (Platform.isPhysicalClient() && ClientProxy.hasItem()) ? Shapes.block() : Shapes.empty();
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (ClientProxy.hasItem()) {
			float x = pos.getX() + 0.3F + random.nextFloat() * 0.4F;
			float y = pos.getY() + 0.5F;
			float z = pos.getZ() + 0.3F + random.nextFloat() * 0.4F;

			int color = CommonConfig.defaultLightColor;
			if (CoreModule.COLORED_LIGHT.is(state) && level.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
				color = be.getColor();
			}
			level.addParticle(new DustParticleOptions(color, 1.0F), x, y, z, 0, 0, 0);
		}
	}

	public int getColor(BlockState stateIn, Level worldIn, BlockPos pos) {
		return CommonConfig.defaultLightColor;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, LIGHT);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}
}
