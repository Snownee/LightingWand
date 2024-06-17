package snownee.lightingwand;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredLightBlock extends LightBlock implements EntityBlock {
	public static final MapCodec<ColoredLightBlock> CODEC = simpleCodec(ColoredLightBlock::new);

	public ColoredLightBlock(Properties properties) {
		super(properties);
	}

	public int getColor(BlockState stateIn, Level worldIn, BlockPos pos) {
		if (worldIn.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
			return be.getColor();
		}
		return CommonConfig.defaultLightColor;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CoreModule.LIGHT_TILE.get().create(pos, state);
	}

	@Override
	protected MapCodec<? extends Block> codec() {
		return CODEC;
	}
}
