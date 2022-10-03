package snownee.lightingwand.common;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import snownee.lightingwand.CommonConfig;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.compat.ShimmerCompat;

public class LightEntity extends ThrowableProjectile {
	private static final EntityDataAccessor<Integer> DATA_LIGHT = SynchedEntityData.defineId(LightEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(LightEntity.class, EntityDataSerializers.INT);

	public Object shimmerLight;

	public LightEntity(EntityType<?> type, Level levelIn) {
		this(levelIn);
	}

	public LightEntity(Level levelIn) {
		super(CoreModule.PROJECTILE, levelIn);
	}

	public LightEntity(Level levelIn, LivingEntity owner) {
		super(CoreModule.PROJECTILE, owner, levelIn);
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
	protected void onHit(HitResult result) {
		if (!level.isClientSide && result != null) {
			discard();
			BlockPos pos = null;
			switch (result.getType()) {
			case MISS:
				return;
			case ENTITY:
				pos = new BlockPos(result.getLocation());
				break;
			case BLOCK:
				pos = ((BlockHitResult) result).getBlockPos().relative(((BlockHitResult) result).getDirection());
				break;
			}

			if (level.getBlockState(pos).getMaterial().isReplaceable()) {
				FluidState fluidstate = level.getFluidState(pos);
				int color = getColor();
				Block block = color == 0 ? CoreModule.LIGHT.get() : CoreModule.COLORED_LIGHT.get();
				if (level.setBlock(pos, block.defaultBlockState().setValue(LightBlock.LIGHT, Mth.clamp(getLightValue(), 1, 15)).setValue(LightBlock.WATERLOGGED, fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8), 11)) {
					if (color != 0 && level.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
						be.setColor(color);
					}
					level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
				}
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide && !onGround) {
			Vec3 motion = getDeltaMovement();
			Vector3f color = CommonConfig.intColorToVector3(getColor());
			for (int k = 0; k < 2; ++k) {
				level.addParticle(new DustParticleOptions(color, 1.0F), getX() + motion.x * k / 2D, getY() + motion.y * k / 2D, getZ() + motion.z * k / 2D, 0, 0, 0);
			}
			if (shimmerLight != null) {
				ShimmerCompat.updateLight(this);
			}
		}
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_LIGHT, 15);
		this.entityData.define(DATA_COLOR, 0);
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		setLightValue(compound.getInt("Light"));
		setColor(compound.getInt("Color"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Light", getLightValue());
		int color = getColor();
		if (color != 0) {
			compound.putInt("Color", color);
		}
	}

	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
		if (level.isClientSide && CoreModule.shimmerCompat && shimmerLight == null) {
			ShimmerCompat.addLight(this);
		}
	}

	@Override
	public void onClientRemoval() {
		super.onClientRemoval();
		if (shimmerLight != null) {
			ShimmerCompat.removeLight(this);
		}
	}

	public void setLightValue(int lightValue) {
		if (lightValue == 0) {
			lightValue = 15;
		}
		entityData.set(DATA_LIGHT, lightValue);
	}

	public void setColor(int color) {
		entityData.set(DATA_COLOR, color);
	}

	public int getLightValue() {
		return entityData.get(DATA_LIGHT);
	}

	public int getColor() {
		return entityData.get(DATA_COLOR);
	}
}
