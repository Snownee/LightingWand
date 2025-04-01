package snownee.lightingwand;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.block.entity.ModBlockEntity;
import snownee.kiwi.util.NotNullByDefault;
import snownee.lightingwand.compat.ShimmerCompat;
import snownee.lightingwand.util.CommonProxy;

@NotNullByDefault
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
	protected @NotNull CompoundTag writePacketData(CompoundTag data, HolderLookup.Provider provider) {
		data.putInt("Color", color);
		return data;
	}

	@Override
	protected void loadAdditional(CompoundTag data, HolderLookup.Provider provider) {
		readPacketData(data);
		super.loadAdditional(data, provider);
	}

	@Override
	protected void saveAdditional(CompoundTag data, HolderLookup.Provider provider) {
		writePacketData(data, provider);
		super.saveAdditional(data, provider);
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
		if (level != null && level.isClientSide && shimmerLight != null) {
			ShimmerCompat.removeLight(this);
		}
	}
}
