package snownee.lightingwand;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.block.entity.ModBlockEntity;
import snownee.lightingwand.compat.ShimmerCompat;
import snownee.lightingwand.util.CommonProxy;

public class ColoredLightBlockEntity extends ModBlockEntity {

	public Object shimmerLight;
	private int color = CommonConfig.defaultLightColor;

	public ColoredLightBlockEntity(BlockPos pos, BlockState state) {
		super(CoreModule.LIGHT_TILE.get(), pos, state);
	}

	@Override
	protected void readPacketData(CompoundTag data) {
		color = data.getInt("Color");
		if (CommonProxy.shimmerCompat && level != null && level.isClientSide) {
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

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		refresh();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setBlockState(BlockState newState) {
		super.setBlockState(newState);
		if (CommonProxy.shimmerCompat && level != null && level.isClientSide) {
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
