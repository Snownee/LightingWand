package snownee.lightingwand;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredLightBlock extends LightBlock implements EntityBlock {
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

}
