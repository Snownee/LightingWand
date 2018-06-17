package snownee.lightingwand.common;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockDispenser;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.lightingwand.Config;
import snownee.lightingwand.LW;

public class ItemWand extends Item
{
    public ItemWand()
    {
        super();
        setRegistryName(LW.MODID, "wand");
        setUnlocalizedName(LW.MODID + ".wand");
        setMaxStackSize(1);
        setMaxDamage(Config.config.getInt("wandDuration", Config.catGeneral, 255, 1, Integer.MAX_VALUE, "Max duration of wand"));
        setCreativeTab(CreativeTabs.TOOLS);
        setNoRepair();
        addPropertyOverride(new ResourceLocation("broken"), new IItemPropertyGetter()
        {
            @Override
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                return ItemWand.isUsable(stack) ? 0 : 1;
            }
        });
        if (Config.registerWand && Config.shootProjectile)
        {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new IBehaviorDispenseItem()
            {
                @Override
                public ItemStack dispense(IBlockSource source, ItemStack stack)
                {
                    if (ItemWand.isUsable(stack))
                    {
                        World world = source.getWorld();
                        IPosition iposition = BlockDispenser.getDispensePosition(source);
                        EnumFacing enumfacing = source.getBlockState().getValue(BlockDispenser.FACING);
                        EntityLight entity = new EntityLight(world, iposition.getX(), iposition.getY(), iposition.getZ());
                        entity.shoot(enumfacing.getFrontOffsetX(), enumfacing.getFrontOffsetY()
                                + 0.1F, enumfacing.getFrontOffsetZ(), 1.3F + world.rand.nextFloat() * 0.4F, 0);
                        entity.motionX += world.rand.nextGaussian() * 0.1D;
                        entity.motionZ += world.rand.nextGaussian() * 0.1D;
                        world.spawnEntity(entity);
                        stack.attemptDamageItem(1, world.rand, null);
                    }
                    return stack;
                }
            });
        }
    }

    public static boolean isUsable(ItemStack stack)
    {
        return stack.getItemDamage() < stack.getMaxItemUseDuration();
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return getMaxDamage(stack);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (ItemWand.isUsable(stack))
        {
            RayTraceResult rayTraceResult = rayTrace(worldIn, playerIn, true);
            if (rayTraceResult != null)
            {
                BlockPos pos = rayTraceResult.getBlockPos().offset(rayTraceResult.sideHit);

                if (!playerIn.canPlayerEdit(pos, playerIn.getAdjustedHorizontalFacing(), stack)
                        || !worldIn.mayPlace(ModConstants.LIGHT, pos, true, rayTraceResult.sideHit, playerIn))
                {
                    return new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
                }
                if (worldIn.isAirBlock(pos))
                {
                    playerIn.getHeldItem(handIn).damageItem(1, playerIn);
                    worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat()
                            * 0.4F + 0.8F);
                    if (!worldIn.isRemote)
                    {
                        worldIn.setBlockState(pos, ModConstants.LIGHT.getDefaultState(), 11);
                    }
                }
            }
            else if (Config.shootProjectile)
            {
                // TODO: Sound subtitle
                worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.8F, 0.4F
                        / (itemRand.nextFloat() * 0.4F + 0.8F));

                if (!worldIn.isRemote)
                {
                    EntityLight entity = new EntityLight(worldIn, playerIn);
                    entity.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0, 1.5F, 0);
                    worldIn.spawnEntity(entity);
                }

                playerIn.getHeldItem(handIn).damageItem(1, playerIn);
                playerIn.addStat(StatList.getObjectUseStats(this));
            }
            if (!ItemWand.isUsable(stack))
            {
                worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.8F
                        + worldIn.rand.nextFloat() * 0.4F);
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
        }
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (!ItemWand.isUsable(stack))
        {
            tooltip.add(TextFormatting.DARK_RED + "" + TextFormatting.BOLD
                    + I18n.format("tip." + LW.MODID + ".uncharged"));
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        if (!isUsable(stack))
        {
            return false;
        }
        return super.showDurabilityBar(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return enchantment.getName().equals("enchantment.vanishing_curse");
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
    {
        if (ItemWand.isUsable(stack))
        {
            target.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 200));
            stack.damageItem(1, attacker);
            return true;
        }
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
    {
        return new ICapabilityProvider()
        {
            @Override
            public boolean hasCapability(Capability<?> capability, EnumFacing facing)
            {
                return Config.energyPerUse != 0 && capability == CapabilityEnergy.ENERGY;
            }

            @Override
            public <T> T getCapability(Capability<T> capability, EnumFacing facing)
            {
                if (hasCapability(capability, facing))
                {
                    return CapabilityEnergy.ENERGY.cast(new EnergyRepair(stack));
                }
                return null;
            }
        };
    }
}
