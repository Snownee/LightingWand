package snownee.lightingwand.common;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class LightEntity extends ThrowableEntity {
	public int lightValue = 15;

	public LightEntity(EntityType<?> type, World worldIn) {
		this(worldIn);
	}

	public LightEntity(World worldIn) {
		super(ModConstants.LIGHT_ENTITY_TYPE, worldIn);
	}

	public LightEntity(World worldIn, LivingEntity throwerIn) {
		super(ModConstants.LIGHT_ENTITY_TYPE, throwerIn, worldIn);
	}

	public LightEntity(World worldIn, double x, double y, double z) {
		super(ModConstants.LIGHT_ENTITY_TYPE, x, y, z, worldIn);
	}

	@Override
	protected float getGravityVelocity() {
		return 0.01F;
	}

	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
		float f = MathHelper.sqrt(x * x + y * y + z * z);
		setMotion(x / f * velocity, y / f * velocity, z / f * velocity);
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (!world.isRemote && result != null) {
			remove();
			BlockPos pos = null;
			switch (result.getType()) {
			case MISS:
				return;
			case ENTITY:
				pos = new BlockPos(result.getHitVec());
				break;
			case BLOCK:
				pos = ((BlockRayTraceResult) result).getPos().offset(((BlockRayTraceResult) result).getFace());
				break;
			}

			if (world.getBlockState(pos).getMaterial().isReplaceable()) {
				FluidState fluidstate = world.getFluidState(pos);
				if (world.setBlockState(pos, ModConstants.LIGHT.getDefaultState().with(LightBlock.LIGHT, MathHelper.clamp(lightValue, 1, 15)).with(LightBlock.WATERLOGGED, fluidstate.isTagged(FluidTags.WATER) && fluidstate.getLevel() == 8), 11)) {
					world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
				}
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (world.isRemote && !onGround) {
			Vector3d motion = getMotion();
			for (int k = 0; k < 2; ++k) {
				world.addParticle(new RedstoneParticleData(1, 1, 0, 1.0F), getPosX() + motion.x * k / 2D, getPosY() + motion.y * k / 2D, getPosZ() + motion.z * k / 2D, 0, 0, 0);
			}
		}
	}

	@Override
	protected void registerData() {
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		lightValue = compound.getInt("Light");
		if (lightValue == 0) {
			lightValue = 15;
		}
	}

	@Override
	protected void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("Light", lightValue);
	}
}
