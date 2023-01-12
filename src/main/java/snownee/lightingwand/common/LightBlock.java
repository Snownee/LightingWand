package snownee.lightingwand.common;

import org.joml.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import snownee.lightingwand.CommonConfig;
import snownee.lightingwand.CoreModule;

public class LightBlock extends Block implements SimpleWaterloggedBlock {
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
	public boolean isAir(BlockState state) {
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return (FMLEnvironment.dist.isClient() && EffectiveSide.get() == LogicalSide.CLIENT && hasItem()) ? Shapes.block() : Shapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockGetter p_220071_2_, BlockPos p_220071_3_, CollisionContext p_220071_4_) {
		return Shapes.empty();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
		if (hasItem()) {
			float x = pos.getX() + 0.3F + rand.nextFloat() * 0.4F;
			float y = pos.getY() + 0.5F;
			float z = pos.getZ() + 0.3F + rand.nextFloat() * 0.4F;

			Vector3f colorVector = CommonConfig.getDefaultLightColor();
			if (CoreModule.COLORED_LIGHT.is(stateIn) && worldIn.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
				colorVector = CommonConfig.intColorToVector3(colorVector, be.getColor());
			}
			worldIn.addParticle(new DustParticleOptions(colorVector, 1.0F), x, y, z, 0, 0, 0);
		}
	}

	public int getColor(BlockState stateIn, Level worldIn, BlockPos pos) {
		return CommonConfig.defaultLightColor;
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean hasItem() {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return false;
		}
		Item main = player.getMainHandItem().getItem();
		Item off = player.getOffhandItem().getItem();
		if (CoreModule.WAND.is(main) || CoreModule.WAND.is(off)) {
			return true;
		}
		//		if (CommonRegistry.psiCompat) {
//			if (main instanceof ICAD || off instanceof ICAD) {
//				return true;
//			}
//		}
		return false;
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
