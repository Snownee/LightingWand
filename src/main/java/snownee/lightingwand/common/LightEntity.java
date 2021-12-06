package snownee.lightingwand.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import snownee.lightingwand.CoreModule;

public class LightEntity extends ThrowableProjectile {
	public int lightValue = 15;

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
				if (level.setBlock(pos, CoreModule.LIGHT.defaultBlockState().setValue(LightBlock.LIGHT, Mth.clamp(lightValue, 1, 15)).setValue(LightBlock.WATERLOGGED, fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8), 11)) {
					level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
				}
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide && !onGround) {
			Vec3 motion = getDeltaMovement();
			for (int k = 0; k < 2; ++k) {
				level.addParticle(new DustParticleOptions(LightBlock.COLOR_VEC, 1.0F), getX() + motion.x * k / 2D, getY() + motion.y * k / 2D, getZ() + motion.z * k / 2D, 0, 0, 0);
			}
		}
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		lightValue = compound.getInt("Light");
		if (lightValue == 0) {
			lightValue = 15;
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("Light", lightValue);
	}
}
