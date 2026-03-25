package snownee.lightingwand;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import snownee.kiwi.util.PreventUpdateAnimation;
import snownee.lightingwand.util.CommonProxy;

public class WandItem extends Item implements PreventUpdateAnimation {
	public WandItem(Item.Properties properties) {
		super(properties);
	}

	public static boolean isUsable(ItemStack stack) {
		return stack.getDamageValue() < stack.getMaxDamage();
	}

	public static int getLightValue(ItemStack stack) {
		return stack.getOrDefault(CoreModule.WAND_ITEM_DATA.get(), WandItemData.DEFAULT).light();
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!isUsable(stack)) {
			return InteractionResult.FAIL;
		}
		if (!level.isClientSide()) {
			BlockHitResult rayTraceResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
			if (rayTraceResult.getType() == HitResult.Type.BLOCK) {
				BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
				if (!player.mayUseItemAt(pos, player.getMotionDirection(), stack)) {
					return InteractionResult.FAIL;
				}
				BlockState state = level.getBlockState(pos);
				if (!CoreModule.isLightBlock(state) && state.canBeReplaced()) {
					level.playSound(
							null,
							pos,
							SoundEvents.FROGLIGHT_PLACE,
							SoundSource.BLOCKS,
							1.0F,
							player.getRandom().nextFloat() * 0.4F + 0.8F);
					FluidState fluidstate = level.getFluidState(pos);
					OptionalInt color = getCustomColor(stack);
					Block block = color.isEmpty() ? CoreModule.LIGHT.get() : CoreModule.COLORED_LIGHT.get();
					level.setBlock(
							pos,
							block.defaultBlockState()
									.setValue(LightBlock.LIGHT, getLightValue(stack))
									.setValue(LightBlock.WATERLOGGED, fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8),
							11);
					if (color.isPresent() && level.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
						be.setColor(color.getAsInt());
					}
				}
			} else if (rayTraceResult.getType() == HitResult.Type.MISS && CommonConfig.shootProjectile) {
				// TODO: Sound subtitle
				level.playSound(
						null,
						player.getX(),
						player.getY(),
						player.getZ(),
						SoundEvents.EGG_THROW,
						SoundSource.PLAYERS,
						0.8F,
						0.4F / (player.getRandom().nextFloat() * 0.4F + 0.8F));
				LightEntity entity = new LightEntity(level);
				entity.setPos(player.getX(), player.getEyeY() - 0.1F, player.getZ());
				entity.setOwner(player);
				entity.setLightValue(getLightValue(stack));
				entity.setColor(getCustomColor(stack).orElse(CommonConfig.defaultLightColor));
				entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 0);
				level.addFreshEntity(entity);
			}
			stack.hurtAndBreak(1, player, hand);
			if (!isUsable(stack)) {
				level.playSound(
						null,
						player.getX(),
						player.getY(),
						player.getZ(),
						SoundEvents.ITEM_BREAK,
						SoundSource.NEUTRAL,
						0.5F,
						0.8F + level.getRandom().nextFloat() * 0.4F);
			}
			player.awardStat(Stats.ITEM_USED.get(this));
		}
		return InteractionResult.SUCCESS_SERVER;
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
		WandItemData data = stack.getOrDefault(CoreModule.WAND_ITEM_DATA.get(), WandItemData.DEFAULT);
		if (CoreModule.COLORED_LIGHT.is(state) && context.getHand() == InteractionHand.MAIN_HAND &&
				player.getOffhandItem().is(Items.GLASS_PANE)) {
			float alpha = data.alpha();
			if (alpha > .91F) {
				alpha = 0;
			}
			alpha = Mth.clamp(alpha + .1F, 0, 1);
			stack.set(CoreModule.WAND_ITEM_DATA.get(), new WandItemData(data.light(), alpha));
			if (worldIn.getBlockEntity(pos) instanceof ColoredLightBlockEntity be) {
				be.setColor(getColor(stack));
			}
			player.sendOverlayMessage(Component.translatable("tip.lightingwand.opacity", (int) (alpha * 100)));
		} else {
			int wandLight = WandItem.getLightValue(stack);
			int blockLight = state.getValue(LightBlock.LIGHT);
			if (wandLight != blockLight) {
				worldIn.setBlockAndUpdate(pos, state.setValue(LightBlock.LIGHT, wandLight));
			} else {
				wandLight = wandLight % 15 + 1;
				stack.set(CoreModule.WAND_ITEM_DATA.get(), new WandItemData(wandLight, data.alpha()));
				worldIn.setBlockAndUpdate(pos, state.setValue(LightBlock.LIGHT, wandLight));
				player.sendOverlayMessage(Component.translatable("tip.lightingwand.light", wandLight));
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(
			ItemStack itemStack,
			TooltipContext context,
			TooltipDisplay display,
			Consumer<Component> builder,
			TooltipFlag tooltipFlag) {
		if (!isUsable(itemStack)) {
			builder.accept(Component.translatable("tip.lightingwand.uncharged").withStyle(ChatFormatting.DARK_RED));
		}
		if (itemStack.has(DataComponents.DYED_COLOR)) {
			if (CommonProxy.shimmerCompat) {
				builder.accept(Component.translatable(
								"tip.lightingwand.color",
								Component.literal("■").withStyle($ -> $.withColor(getColor(itemStack))))
						.withStyle(ChatFormatting.GRAY));
			} else {
				builder.accept(Component.translatable("tip.lightingwand.noShimmer").withStyle(ChatFormatting.DARK_RED));
			}
		}
		builder.accept(Component.translatable("tip.lightingwand.light", getLightValue(itemStack)).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		if (!isUsable(stack)) {
			return false;
		}
		return super.isBarVisible(stack);
	}

	@Override
	public void hurtEnemy(ItemStack itemStack, LivingEntity mob, LivingEntity attacker) {
		if (isUsable(itemStack)) {
			mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
			if (attacker instanceof Player && !((Player) attacker).isCreative()) {
				itemStack.setDamageValue(itemStack.getDamageValue() + 1);
			}
		}
	}

	public int getColor(ItemStack stack) {
		return getCustomColor(stack).orElse(CommonConfig.defaultLightColor);
	}

	public OptionalInt getCustomColor(ItemStack stack) {
		if (stack.has(DataComponents.DYED_COLOR)) {
			float alpha = stack.getOrDefault(CoreModule.WAND_ITEM_DATA.get(), WandItemData.DEFAULT).alpha();
			if (alpha == 0) {
				alpha = 1;
			}
			int color = Objects.requireNonNull(stack.get(DataComponents.DYED_COLOR)).rgb();
			return OptionalInt.of(((int) (alpha * 255) << 24) + color);
		}
		return OptionalInt.empty();
	}
}
