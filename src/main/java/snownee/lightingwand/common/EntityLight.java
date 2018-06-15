package snownee.lightingwand.common;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityLight extends EntityThrowable
{
    public EntityLight(World worldIn)
    {
        super(worldIn);
        setSize(0, 0);
    }

    public EntityLight(World worldIn, EntityLivingBase throwerIn)
    {
        super(worldIn, throwerIn);
        setSize(0, 0);
    }

    public EntityLight(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
        setSize(0, 0);
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    protected float getGravityVelocity()
    {
        return 0.01F;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        motionX = x / f * velocity;
        motionY = y / f * velocity;
        motionZ = z / f * velocity;
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        if (!world.isRemote && result != null)
        {
            setDead();
            BlockPos pos = result.entityHit != null ? new BlockPos(result.hitVec)
                    : result.getBlockPos().offset(result.sideHit);

            if (world.isAirBlock(pos) && world.mayPlace(ModConstants.LIGHT, pos, true, result.sideHit, thrower))
            {
                if (world.setBlockState(pos, ModConstants.LIGHT.getDefaultState(), 11))
                {
                    world.playSound(null, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat()
                            * 0.4F + 0.8F);
                }
            }
        }
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (world.isRemote && !inGround)
        {
            for (int k = 0; k < 2; ++k)
            {
                this.world.spawnParticle(EnumParticleTypes.REDSTONE, posX + motionX * k / 2D, posY
                        + motionY * k / 2D, posZ + motionZ * k / 2D, 1, 1, 0);
            }
        }
    }
}
