package snownee.lightingwand;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.action.CustomAll;
import net.minecraft.server.dialog.input.NumberRangeInput;
import net.minecraft.server.level.ServerPlayer;
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
	public static final Identifier LIGHT_DIALOG_ACTION = LW.id("set_light");

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
			if (player instanceof ServerPlayer serverPlayer) {
				openLightDialog(serverPlayer, context.getHand(), pos, state.getValue(LightBlock.LIGHT));
			}
		}
		return InteractionResult.SUCCESS;
	}

	private static void openLightDialog(ServerPlayer player, InteractionHand hand, BlockPos pos, int light) {
		CompoundTag additions = new CompoundTag();
		additions.putInt("x", pos.getX());
		additions.putInt("y", pos.getY());
		additions.putInt("z", pos.getZ());
		additions.putInt("hand", hand.ordinal());
		additions.putString("dimension", player.level().dimension().identifier().toString());

		NumberRangeInput input = new NumberRangeInput(
				310,
				Component.translatable("gui.lightingwand.brightness"),
				"options.generic_value",
				new NumberRangeInput.RangeInfo(1, 15, Optional.of((float) light), Optional.of(1F)));
		ActionButton apply = new ActionButton(
				new CommonButtonData(Component.translatable("gui.lightingwand.apply"), 150),
				Optional.of(new CustomAll(LIGHT_DIALOG_ACTION, Optional.of(additions))));
		CommonDialogData common = new CommonDialogData(
				Component.translatable("gui.lightingwand.adjust_light"),
				Optional.empty(),
				true,
				false,
				DialogAction.CLOSE,
				List.of(),
				List.of(new Input("light", input)));
		player.openDialog(Holder.direct(new NoticeDialog(common, apply)));
	}

	public static void handleLightDialogAction(ServerPlayer player, Tag payload) {
		if (!(payload instanceof CompoundTag tag)) {
			LW.LOGGER.warn("Rejected Lighting Wand dialog action without a compound payload from {}", player.getGameProfile().name());
			return;
		}

		Optional<Integer> x = tag.getInt("x");
		Optional<Integer> y = tag.getInt("y");
		Optional<Integer> z = tag.getInt("z");
		Optional<Integer> handId = tag.getInt("hand");
		Optional<String> dimension = tag.getString("dimension");
		Optional<Float> lightValue = tag.getFloat("light");
		if (x.isEmpty() || y.isEmpty() || z.isEmpty() || handId.isEmpty() || dimension.isEmpty() || lightValue.isEmpty()) {
			LW.LOGGER.warn("Rejected incomplete Lighting Wand dialog action from {}", player.getGameProfile().name());
			return;
		}

		int light = Math.round(lightValue.get());
		if (light < 1 || light > 15 || (handId.get() != 0 && handId.get() != 1)) {
			LW.LOGGER.warn("Rejected out-of-range Lighting Wand dialog action from {}", player.getGameProfile().name());
			return;
		}

		if (!player.level().dimension().identifier().toString().equals(dimension.get())) {
			return;
		}
		BlockPos pos = new BlockPos(x.get(), y.get(), z.get());
		if (!player.isWithinBlockInteractionRange(pos, player.blockInteractionRange())) {
			return;
		}
		InteractionHand hand = handId.get() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.is(CoreModule.WAND.get())) {
			return;
		}

		BlockState state = player.level().getBlockState(pos);
		if (!CoreModule.isLightBlock(state)) {
			return;
		}
		if (state.getValue(LightBlock.LIGHT) != light) {
			player.level().setBlockAndUpdate(pos, state.setValue(LightBlock.LIGHT, light));
		}
		WandItemData data = stack.getOrDefault(CoreModule.WAND_ITEM_DATA.get(), WandItemData.DEFAULT);
		if (data.light() != light) {
			stack.set(CoreModule.WAND_ITEM_DATA.get(), new WandItemData(light, data.alpha()));
		}
		player.sendOverlayMessage(Component.translatable("tip.lightingwand.light", light));
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
