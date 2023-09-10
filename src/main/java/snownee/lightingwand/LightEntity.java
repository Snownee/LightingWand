package snownee.lightingwand;

import org.joml.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import snownee.lightingwand.compat.ShimmerCompat;
import snownee.lightingwand.util.CommonProxy;

public class LightEntity extends ThrowableProjectile {
	private static final EntityDataAccessor<Integer> DATA_LIGHT = SynchedEntityData.defineId(LightEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(LightEntity.class, EntityDataSerializers.INT);

	public Object shimmerLight;
	private boolean discardNextTick;

	public LightEntity(EntityType<?> type, Level levelIn) {
		this(levelIn);
	}

	public LightEntity(Level levelIn) {
		super(CoreModule.PROJECTILE.get(), levelIn);
	}

	public LightEntity(Level levelIn, LivingEntity owner) {
		super(CoreModule.PROJECTILE.get(), owner, levelIn);
	}

	@Override
	protected float getGravity() {
		return 0.01F;
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		float f = Mth.sqrt((float) (x * x + y * y + z * z));
		setDeltaMovement(x / f * velocity, y / f * velocity, z / f * velocity);
	}

	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		placeLight(blockHitResult.getBlockPos().relative(blockHitResult.getDirection()));
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		placeLight(BlockPos.containing(entityHitResult.getLocation()));
	}

	private void placeLight(BlockPos pos) {
		Level level = level();
		if (discardNextTick || level.isClientSide || !level.getBlockState(pos).canBeReplaced()) {
			return;
		}
		FluidState fluidstate = level.getFluidState(pos);
		int color = getColor();
		Block block = color == 0 ? CoreModule.LIGHT.get() : CoreModule.COLORED_LIGHT.get();
		if (level.setBlock(pos, block.defaultBlockState().setValue(LightBlock.LIGHT, Mth.clamp(getLightValue(), 1, 15)).setValue(LightBlock.WATERLOGGED, fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8), 11)) {
			if (color != 0 && level.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
				be.setColor(color);
			}
			level.playSound(null, pos, SoundEvents.FROGLIGHT_PLACE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
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
		if (level().isClientSide && !onGround()) {
			if (CommonProxy.shimmerCompat && shimmerLight == null) {
				ShimmerCompat.addLight(this);
			} else if (shimmerLight != null) {
				ShimmerCompat.updateLight(this);
			}
			Vec3 motion = getDeltaMovement();
			Vector3f color = CommonConfig.intColorToVector3(getColor());
			for (int k = 0; k < 2; ++k) {
				level().addParticle(new DustParticleOptions(color, 1.0F), getX() + motion.x * k / 2D, getY() + motion.y * k / 2D, getZ() + motion.z * k / 2D, 0, 0, 0);
			}
		}
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_LIGHT, 15);
		this.entityData.define(DATA_COLOR, 0);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return CommonProxy.getAddEntityPacket(this);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		setLightValue(compound.getInt("Light"));
		setColor(compound.getInt("Color"));
		discardNextTick = compound.getBoolean("Discard");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Light", getLightValue());
		int color = getColor();
		if (color != 0) {
			compound.putInt("Color", color);
		}
		if (discardNextTick) {
			compound.putBoolean("Discard", true);
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
