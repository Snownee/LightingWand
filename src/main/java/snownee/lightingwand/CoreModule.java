package snownee.lightingwand;

import com.mojang.serialization.MapCodec;

import it.unimi.dsi.fastutil.objects.ReferenceSortedSets;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.BlockObject;
import snownee.kiwi.Categories;
import snownee.kiwi.ItemObject;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.loader.event.InitEvent;
import snownee.lightingwand.util.CommonProxy;

@KiwiModule
public class CoreModule extends AbstractModule {

	@NoItem
	public static final BlockObject<Block> LIGHT = block($ -> new LightBlock($.replaceable()
			.noCollision()
			.noLootTable()
			.instabreak()
			.noTerrainParticles()
			.lightLevel(state -> state.getValue(LightBlock.LIGHT))
			.sound(SoundType.FROGLIGHT)));
	@Name("light")
	public static final KiwiGO<MapCodec<LightBlock>> LIGHT_CODEC = go(() -> LightBlock.CODEC, Registries.BLOCK_TYPE);
	@NoItem
	public static final BlockObject<Block> COLORED_LIGHT = block(
			$ -> new ColoredLightBlock(CommonProxy.shimmerCompat ?
					$.lightLevel(_ -> 0) :
					$), LIGHT);
	@Name("colored_light")
	public static final KiwiGO<MapCodec<ColoredLightBlock>> COLORED_LIGHT_CODEC = go(() -> ColoredLightBlock.CODEC, Registries.BLOCK_TYPE);
	public static final KiwiGO<DataComponentType<WandItemData>> WAND_ITEM_DATA = go(
			() -> DataComponentType.<WandItemData>builder()
					.persistent(WandItemData.CODEC)
					.networkSynchronized(WandItemData.STREAM_CODEC)
					.build(), Registries.DATA_COMPONENT_TYPE);
	@Category(Categories.TOOLS_AND_UTILITIES)
	public static final ItemObject<WandItem> WAND = item($ -> new WandItem($.durability(CommonConfig.wandDurability)
			.component(WAND_ITEM_DATA.getOrCreate(), WandItemData.DEFAULT)
			.component(
					DataComponents.TOOLTIP_DISPLAY,
					new TooltipDisplay(false, ReferenceSortedSets.singleton(DataComponents.DYED_COLOR)))));
	public static final KiwiGO<RecipeSerializer<RepairRecipe>> REPAIR = go(RepairRecipe.Serializer::new);
	@Name("light")
	public static final KiwiGO<EntityType<LightEntity>> PROJECTILE = entity(key -> EntityType.Builder
			.of(LightEntity::new, MobCategory.MISC)
			.sized(0.25F, 0.25F)
			.fireImmune()
			.noLootTable()
			.clientTrackingRange(4)
			.updateInterval(10)
			.build(key));
	@Name("light")
	public static final KiwiGO<BlockEntityType<ColoredLightBlockEntity>> LIGHT_TILE = blockEntity(
			ColoredLightBlockEntity::new,
			COLORED_LIGHT);

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isLightBlock(BlockState state) {
		return LIGHT.is(state) || COLORED_LIGHT.is(state);
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			if (CommonConfig.shootProjectile) {
				DispenserBlock.registerBehavior(
						WAND.get(), (source, stack) -> {
							ServerLevel world = source.level();
							if (!WandItem.isUsable(stack)) {
								return stack;
							}
							Position iposition = DispenserBlock.getDispensePosition(source);
							Direction Direction = source.state().getValue(DispenserBlock.FACING);
							LightEntity entity = new LightEntity(world);
							entity.setPos(iposition.x(), iposition.y(), iposition.z());
							entity.setLightValue(WandItem.getLightValue(stack));
							entity.setColor(WAND.get().getCustomColor(stack).orElse(CommonConfig.defaultLightColor));
							entity.shoot(
									Direction.getStepX(),
									Direction.getStepY() + 0.1F,
									Direction.getStepZ(),
									1.3F + world.getRandom().nextFloat() * 0.4F,
									0);
							Vec3 motion = entity.getDeltaMovement();
							entity.setDeltaMovement(motion.add(
									world.getRandom().nextGaussian() * 0.1D,
									0,
									world.getRandom().nextGaussian() * 0.1D));
							world.addFreshEntity(entity);
							stack.hurtAndBreak(1, world, null, _ -> {});
							return stack;
						});
			}
			CauldronInteraction.WATER.map().put(
					WAND.get(), (blockState, level, blockPos, player, interactionHand, itemStack) -> {
						if (!itemStack.is(ItemTags.DYEABLE)) {
							return InteractionResult.TRY_WITH_EMPTY_HAND;
						} else if (!itemStack.has(DataComponents.DYED_COLOR)) {
							return InteractionResult.TRY_WITH_EMPTY_HAND;
						} else {
							if (!level.isClientSide()) {
								itemStack.remove(DataComponents.DYED_COLOR);
								itemStack.update(
										WAND_ITEM_DATA.get(),
										WandItemData.DEFAULT,
										$ -> new WandItemData($.light(), WandItemData.DEFAULT.alpha()));
								player.awardStat(Stats.CLEAN_ARMOR);
								LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
							}

							return InteractionResult.SUCCESS_SERVER;
						}
					});
			CommonProxy.postRegister();
		});
	}
}
