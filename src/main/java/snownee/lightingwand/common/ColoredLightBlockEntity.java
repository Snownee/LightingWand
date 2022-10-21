package snownee.lightingwand.common;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.block.entity.BaseBlockEntity;
import snownee.lightingwand.CommonConfig;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.compat.ShimmerCompat;

public class ColoredLightBlockEntity extends BaseBlockEntity {

	private int color = CommonConfig.defaultLightColor;
	public Object shimmerLight;

	public ColoredLightBlockEntity(BlockPos pos, BlockState state) {
		super(CoreModule.LIGHT_TILE.get(), pos, state);
	}

	@Override
	protected void readPacketData(CompoundTag data) {
		color = data.getInt("Color");
		if (CoreModule.shimmerCompat && level != null && level.isClientSide) {
			ShimmerCompat.addLight(this);
		}
	}

	@Override
	protected CompoundTag writePacketData(CompoundTag data) {
		data.putInt("Color", color);
		return data;
	}

	@Override
	public void load(CompoundTag data) {
		readPacketData(data);
		super.load(data);
	}

	@Override
	public void saveAdditional(CompoundTag data) {
		writePacketData(data);
		super.saveAdditional(data);
	}

	public void setColor(int color) {
		this.color = color;
		refresh();
	}

	public int getColor() {
		return color;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setBlockState(BlockState newState) {
		super.setBlockState(newState);
		if (CoreModule.shimmerCompat && level != null && level.isClientSide) {
			ShimmerCompat.addLight(this);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if (level.isClientSide && shimmerLight != null) {
			ShimmerCompat.removeLight(this);
		}
	}
}
