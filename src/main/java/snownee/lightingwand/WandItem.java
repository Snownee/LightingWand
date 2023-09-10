package snownee.lightingwand;

import java.util.List;
import java.util.OptionalInt;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import snownee.lightingwand.util.CommonProxy;

public class WandItem extends Item implements DyeableLeatherItem {
	public WandItem(Properties properties) {
		super(properties);
	}

	public static boolean isUsable(ItemStack stack) {
		return stack.getDamageValue() < stack.getMaxDamage();
	}

	public static int getLightValue(ItemStack stack) {
		if (stack.hasTag() && stack.getTag().contains("Light", Tag.TAG_INT)) {
			return Mth.clamp(stack.getTag().getInt("Light"), 1, 15);
		} else {
			return 15;
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack stack = playerIn.getItemInHand(handIn);
		if (!isUsable(stack)) {
			return InteractionResultHolder.fail(stack);
		}
		if (!worldIn.isClientSide) {
			BlockHitResult rayTraceResult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.NONE);
			if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
				BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
				if (!playerIn.mayUseItemAt(pos, playerIn.getMotionDirection(), stack)) {
					return new InteractionResultHolder<>(InteractionResult.FAIL, playerIn.getItemInHand(handIn));
				}
				BlockState state = worldIn.getBlockState(pos);
				if (!CoreModule.isLightBlock(state) && state.canBeReplaced()) {
					worldIn.playSound(null, pos, SoundEvents.FROGLIGHT_PLACE, SoundSource.BLOCKS, 1.0F, playerIn.getRandom().nextFloat() * 0.4F + 0.8F);
					FluidState fluidstate = worldIn.getFluidState(pos);
					OptionalInt color = getCustomColor(stack);
					Block block = color.isEmpty() ? CoreModule.LIGHT.get() : CoreModule.COLORED_LIGHT.get();
					worldIn.setBlock(pos, block.defaultBlockState().setValue(LightBlock.LIGHT, getLightValue(stack)).setValue(LightBlock.WATERLOGGED, fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8), 11);
					if (color.isPresent() && worldIn.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
						be.setColor(color.getAsInt());
					}
				}
			} else if (rayTraceResult.getType() == HitResult.Type.MISS && CommonConfig.shootProjectile) {
				// TODO: Sound subtitle
				worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.8F, 0.4F / (playerIn.getRandom().nextFloat() * 0.4F + 0.8F));
				LightEntity entity = new LightEntity(worldIn, playerIn);
				entity.setLightValue(getLightValue(stack));
				entity.setColor(getCustomColor(stack).orElse(0));
				entity.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0, 1.5F, 0);
				worldIn.addFreshEntity(entity);
			}
			stack.hurt(1, playerIn.getRandom(), (ServerPlayer) playerIn);
			if (!isUsable(stack)) {
				worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 0.5F, 0.8F + worldIn.random.nextFloat() * 0.4F);
			}
			playerIn.awardStat(Stats.ITEM_USED.get(this));
		}
		return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.isSecondaryUseActive()) {
			return InteractionResult.PASS;
		}
		Level worldIn = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = worldIn.getBlockState(pos);
		if (!CoreModule.isLightBlock(state)) {
			return InteractionResult.PASS;
		}
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		ItemStack stack = context.getItemInHand();
		if (CoreModule.COLORED_LIGHT.is(state) && context.getHand() == InteractionHand.MAIN_HAND && player.getOffhandItem().is(Items.GLASS_PANE)) {
			float alpha = 1;
			if (stack.hasTag() && stack.getOrCreateTag().contains("Alpha")) {
				alpha = stack.getTag().getFloat("Alpha");
			}
			if (alpha > .91F) {
				alpha = 0;
			}
			alpha = Mth.clamp(alpha + .1F, 0, 1);
			if (alpha == 1) {
				stack.removeTagKey("Alpha");
			} else {
				stack.getOrCreateTag().putFloat("Alpha", alpha);
			}
			if (worldIn.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
				be.setColor(getColor(stack));
			}
			player.displayClientMessage(Component.translatable("tip.lightingwand.opacity", (int) (alpha * 100)), true);
		} else {
			int wandLight = WandItem.getLightValue(stack);
			int blockLight = state.getValue(LightBlock.LIGHT);
			if (wandLight != blockLight) {
				worldIn.setBlockAndUpdate(pos, state.setValue(LightBlock.LIGHT, wandLight));
			} else {
				wandLight = wandLight % 15 + 1;
				stack.getOrCreateTag().putInt("Light", wandLight);
				worldIn.setBlockAndUpdate(pos, state.setValue(LightBlock.LIGHT, wandLight));
				player.displayClientMessage(Component.translatable("tip.lightingwand.light", wandLight), true);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (!isUsable(stack)) {
			tooltip.add(Component.translatable("tip.lightingwand.uncharged").withStyle(ChatFormatting.DARK_RED));
		}
		if (hasCustomColor(stack)) {
			if (CommonProxy.shimmerCompat) {
				tooltip.add(Component.translatable("tip.lightingwand.color", Component.literal("■").withStyle($ -> $.withColor(getColor(stack)))).withStyle(ChatFormatting.GRAY));
			} else {
				tooltip.add(Component.translatable("tip.lightingwand.noShimmer").withStyle(ChatFormatting.DARK_RED));
			}
		}
		tooltip.add(Component.translatable("tip.lightingwand.light", getLightValue(stack)).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		if (!isUsable(stack)) {
			return false;
		}
		return super.isBarVisible(stack);
	}

	@Override
	public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
		return false;
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (isUsable(stack)) {
			target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
			if (attacker instanceof Player && !((Player) attacker).isCreative()) {
				stack.setDamageValue(stack.getDamageValue() + 1);
			}
			return true;
		}
		return super.hurtEnemy(stack, target, attacker);
	}

	@Override
	public boolean hasCustomColor(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains("Color", Tag.TAG_ANY_NUMERIC);
	}

	@Override
	public int getColor(ItemStack stack) {
		return getCustomColor(stack).orElse(CommonConfig.defaultLightColor);
	}

	public OptionalInt getCustomColor(ItemStack stack) {
		if (hasCustomColor(stack)) {
			float alpha = stack.getTag().getFloat("Alpha");
			if (alpha == 0) {
				alpha = 1;
			}
			return OptionalInt.of(((int) (alpha * 255) << 24) + stack.getTag().getInt("Color"));
		}
		return OptionalInt.empty();
	}

	@Override
	public void setColor(ItemStack stack, int color) {
		stack.getOrCreateTag().putInt("Color", color);
	}

	@Override
	public void clearColor(ItemStack stack) {
		stack.removeTagKey("Color");
		stack.removeTagKey("Alpha");
	}
}
