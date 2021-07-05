package snownee.lightingwand.common;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import snownee.lightingwand.Config;
import snownee.lightingwand.LW;

public class WandItem extends Item {
    public WandItem() {
        super(new Item.Properties().group(ItemGroup.TOOLS).setNoRepair().maxStackSize(1));
    }

    public static boolean isUsable(ItemStack stack) {
        return stack.getDamage() < stack.getMaxDamage();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (WandItem.isUsable(stack)) {
            RayTraceResult rayTraceResult = rayTrace(worldIn, playerIn, FluidMode.NONE);
            if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
                BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTraceResult;
                BlockPos pos = blockRayTraceResult.getPos().offset(blockRayTraceResult.getFace());

                if (!playerIn.canPlayerEdit(pos, playerIn.getAdjustedHorizontalFacing(), stack)) {
                    return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
                }
                BlockState state = worldIn.getBlockState(pos);
                if (state.getBlock() != ModConstants.LIGHT && state.getMaterial().isReplaceable()) {
                    if (!playerIn.isCreative()) {
                        stack.setDamage(stack.getDamage() + 1);
                    }
                    worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
                    if (!worldIn.isRemote) {
                        FluidState fluidstate = worldIn.getFluidState(pos);
                        worldIn.setBlockState(pos, ModConstants.LIGHT.getDefaultState().with(LightBlock.LIGHT, getLightValue(stack)).with(LightBlock.WATERLOGGED, fluidstate.isTagged(FluidTags.WATER) && fluidstate.getLevel() == 8), 11);
                    }
                }
            } else if (rayTraceResult.getType() == RayTraceResult.Type.MISS && Config.shootProjectile.get()) {
                // TODO: Sound subtitle
                worldIn.playSound((PlayerEntity) null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_EGG_THROW, SoundCategory.PLAYERS, 0.8F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

                ItemStack held = playerIn.getHeldItem(handIn);
                if (!worldIn.isRemote) {
                    LightEntity entity = new LightEntity(worldIn, playerIn);
                    entity.lightValue = getLightValue(stack);
                    entity./*shoot*/func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0, 1.5F, 0);
                    worldIn.addEntity(entity);
                }
                if (!playerIn.isCreative()) {
                    held.setDamage(held.getDamage() + 1);
                }
            }
            if (!WandItem.isUsable(stack)) {
                worldIn.playSound((PlayerEntity) null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.NEUTRAL, 0.5F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
            }
            playerIn.swingArm(handIn);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
        return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (!WandItem.isUsable(stack)) {
            tooltip.add(new StringTextComponent(TextFormatting.DARK_RED + "" + TextFormatting.BOLD + I18n.format("tip." + LW.MODID + ".uncharged")));
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (!isUsable(stack)) {
            return false;
        }
        return super.showDurabilityBar(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (WandItem.isUsable(stack)) {
            target.addPotionEffect(new EffectInstance(Effects.GLOWING, 200));
            if (attacker instanceof PlayerEntity && !((PlayerEntity) attacker).isCreative()) {
                stack.setDamage(stack.getDamage() + 1);
            }
            return true;
        }
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
        return new ICapabilityProvider() {

            private final LazyOptional<EnergyRepair> handler = LazyOptional.of(() -> new EnergyRepair(stack));

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
                if (cap == CapabilityEnergy.ENERGY && Config.energyPerUse.get() > 0) {
                    return handler.cast();
                }
                return LazyOptional.empty();
            }
        };
    }

    public static int getLightValue(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Light", NBT.TAG_INT)) {
            return MathHelper.clamp(stack.getTag().getInt("Light"), 1, 15);
        } else {
            return 15;
        }
    }
}
