package snownee.lightingwand;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import snownee.kiwi.block.entity.ModBlockEntity;
import snownee.lightingwand.compat.ShimmerCompat;
import snownee.lightingwand.util.CommonProxy;

public class ColoredLightBlockEntity extends ModBlockEntity {

	public @Nullable Object shimmerLight;
	private int color = CommonConfig.defaultLightColor;

	public ColoredLightBlockEntity(BlockPos pos, BlockState state) {
		super(CoreModule.LIGHT_TILE.get(), pos, state);
	}

	@Override
	protected void readPacketData(ValueInput valueInput) {
		color = valueInput.getIntOr("Color", CommonConfig.defaultLightColor);
		if (CommonProxy.shimmerCompat && level != null && level.isClientSide()) {
			ShimmerCompat.addLight(this);
		}
	}

	@Override
	protected void writePacketData(ValueOutput valueOutput) {
		valueOutput.putInt("Color", color);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		readPacketData(input);
		super.loadAdditional(input);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		writePacketData(output);
		super.saveAdditional(output);
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		refresh();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setBlockState(BlockState blockState) {
		super.setBlockState(blockState);
		if (CommonProxy.shimmerCompat && level != null && level.isClientSide()) {
			ShimmerCompat.addLight(this);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if (level != null && level.isClientSide() && shimmerLight != null) {
			ShimmerCompat.removeLight(this);
		}
	}
}
