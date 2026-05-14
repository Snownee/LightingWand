package snownee.lightingwand;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import snownee.lightingwand.compat.ShimmerCompat;
import snownee.lightingwand.util.CommonProxy;

public class LightEntity extends ThrowableProjectile {
	private static final EntityDataAccessor<Integer> DATA_LIGHT = SynchedEntityData.defineId(LightEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(LightEntity.class, EntityDataSerializers.INT);

	public @Nullable Object shimmerLight;
	private boolean discardNextTick;

	public LightEntity(EntityType<?> type, Level level) {
		this(level);
	}

	public LightEntity(Level level) {
		super(CoreModule.PROJECTILE.get(), level);
	}

	@Override
	protected double getDefaultGravity() {
		return 0.01;
	}

	@Override
	public void shoot(double xd, double yd, double zd, float pow, float uncertainty) {
		float f = Mth.sqrt((float) (xd * xd + yd * yd + zd * zd));
		setDeltaMovement(xd / f * pow, yd / f * pow, zd / f * pow);
	}

	@Override
	protected void onHitBlock(BlockHitResult hitResult) {
		super.onHitBlock(hitResult);
		placeLight(hitResult.getBlockPos().relative(hitResult.getDirection()));
	}

	@Override
	protected void onHitEntity(EntityHitResult hitResult) {
		super.onHitEntity(hitResult);
		placeLight(BlockPos.containing(hitResult.getLocation()));
	}

	private void placeLight(BlockPos pos) {
		Level level = level();
		if (discardNextTick || level.isClientSide() || !level.getBlockState(pos).canBeReplaced()) {
			return;
		}
		FluidState fluidstate = level.getFluidState(pos);
		int color = getColor();
		Block block = color == 0 ? CoreModule.LIGHT.get() : CoreModule.COLORED_LIGHT.get();
		if (level.setBlock(
				pos,
				block.defaultBlockState()
						.setValue(LightBlock.LIGHT, Mth.clamp(getLightValue(), 1, 15))
						.setValue(LightBlock.WATERLOGGED, fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8),
				11)) {
			if (color != 0 && level.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
				be.setColor(color);
			}
			level.playSound(null, pos, SoundEvents.FROGLIGHT_PLACE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
		}
		discardNextTick = true;
	}

	@Override
	public void tick() {
		if (discardNextTick) {
			discard();
			return;
		}
		super.tick();
		if (level().isClientSide() && !onGround()) {
			if (CommonProxy.shimmerCompat && shimmerLight == null) {
				ShimmerCompat.addLight(this);
			} else if (shimmerLight != null) {
				ShimmerCompat.updateLight(this);
			}
			Vec3 motion = getDeltaMovement();
			for (int k = 0; k < 2; ++k) {
				level().addParticle(
						new DustParticleOptions(getColor(), 1.0F),
						getX() + motion.x * k / 2D,
						getY() + motion.y * k / 2D,
						getZ() + motion.z * k / 2D,
						0,
						0,
						0);
			}
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder entityData) {
		entityData.define(DATA_LIGHT, 15);
		entityData.define(DATA_COLOR, 0);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		setLightValue(input.getIntOr("Light", 15));
		setColor(input.getIntOr("Color", CommonConfig.defaultLightColor));
		discardNextTick = input.getBooleanOr("Discard", false);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putInt("Light", getLightValue());
		int color = getColor();
		if (color != 0) {
			output.putInt("Color", color);
		}
		if (discardNextTick) {
			output.putBoolean("Discard", true);
		}
	}

	@Override
	public void onClientRemoval() {
		super.onClientRemoval();
		if (shimmerLight != null) {
			ShimmerCompat.removeLight(this);
		}
	}

	public int getLightValue() {
		return entityData.get(DATA_LIGHT);
	}

	public void setLightValue(int lightValue) {
		if (lightValue == 0) {
			lightValue = 15;
		}
		entityData.set(DATA_LIGHT, lightValue);
	}

	public int getColor() {
		return entityData.get(DATA_COLOR);
	}

	public void setColor(int color) {
		entityData.set(DATA_COLOR, color);
	}
}
