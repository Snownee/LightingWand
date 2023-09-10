package snownee.lightingwand;

import org.joml.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
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
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.loader.Platform;
import snownee.lightingwand.util.ClientProxy;

public class LightBlock extends ModBlock implements SimpleWaterloggedBlock {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final IntegerProperty LIGHT = IntegerProperty.create("light", 1, 15);

	public LightBlock(Properties properties) {
		super(properties.sound(SoundType.FROGLIGHT));
		registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false).setValue(LIGHT, 15));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return (Platform.isPhysicalClient() && /*EffectiveSide.get() == LogicalSide.CLIENT &&*/ ClientProxy.hasItem()) ? Shapes.block() : Shapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
		return Shapes.empty();
	}

	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
		if (ClientProxy.hasItem()) {
			float x = pos.getX() + 0.3F + rand.nextFloat() * 0.4F;
			float y = pos.getY() + 0.5F;
			float z = pos.getZ() + 0.3F + rand.nextFloat() * 0.4F;

			Vector3f colorVector = CommonConfig.defaultLightColorVector;
			if (CoreModule.COLORED_LIGHT.is(stateIn) && worldIn.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
				colorVector = CommonConfig.intColorToVector3(be.getColor());
			}
			worldIn.addParticle(new DustParticleOptions(colorVector, 1.0F), x, y, z, 0, 0, 0);
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
		return super.getStateForPlacement(context).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

}
