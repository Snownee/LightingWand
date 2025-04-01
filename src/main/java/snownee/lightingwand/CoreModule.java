package snownee.lightingwand;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.loader.event.InitEvent;
import snownee.kiwi.util.KiwiEntityTypeBuilder;
import snownee.lightingwand.util.CommonProxy;

@KiwiModule
public class CoreModule extends AbstractModule {

	@NoItem
	public static final KiwiGO<Block> LIGHT = go(() -> new LightBlock(blockProp().replaceable()
			.noCollission()
			.noLootTable()
			.instabreak()
			.noTerrainParticles()
			.lightLevel(state -> state.getValue(LightBlock.LIGHT))
			.sound(SoundType.FROGLIGHT)));
	@Name("light")
	public static final KiwiGO<MapCodec<LightBlock>> LIGHT_CODEC = go(() -> LightBlock.CODEC, Registries.BLOCK_TYPE);
	@NoItem
	public static final KiwiGO<Block> COLORED_LIGHT = go(() -> new ColoredLightBlock(CommonProxy.shimmerCompat ?
			blockProp(LIGHT.getOrCreate()).lightLevel($ -> 0) :
			blockProp(LIGHT.getOrCreate())));
	@Name("colored_light")
	public static final KiwiGO<MapCodec<ColoredLightBlock>> COLORED_LIGHT_CODEC = go(() -> ColoredLightBlock.CODEC, Registries.BLOCK_TYPE);
	public static final KiwiGO<DataComponentType<WandItemData>> WAND_ITEM_DATA = go(() -> DataComponentType.<WandItemData>builder()
			.persistent(WandItemData.CODEC)
			.networkSynchronized(WandItemData.STREAM_CODEC)
			.build());
	@Category(Categories.TOOLS_AND_UTILITIES)
	public static final KiwiGO<WandItem> WAND = go(() -> new WandItem(itemProp().durability(CommonConfig.wandDurability)
			.component(CoreModule.WAND_ITEM_DATA.getOrCreate(), WandItemData.DEFAULT)));
	public static final KiwiGO<RecipeSerializer<RepairRecipe>> REPAIR = go(RepairRecipe.Serializer::new);
	@Name("light")
	public static final KiwiGO<BlockEntityType<ColoredLightBlockEntity>> LIGHT_TILE = blockEntity(
			ColoredLightBlockEntity::new,
			null,
			COLORED_LIGHT);

	@Name("light")
	public static final KiwiGO<EntityType<LightEntity>> PROJECTILE = go(() -> KiwiEntityTypeBuilder.<LightEntity>create()
			.entityFactory((spawnEntity, world) -> new LightEntity(world))
			.fireImmune()
			.trackRangeChunks(4)
			.trackedUpdateRate(20)
			.forceTrackedVelocityUpdates(true)
			.build());

	public static boolean isLightBlock(BlockState state) {
		return LIGHT.is(state) || COLORED_LIGHT.is(state);
	}

	@Override
	protected void init(InitEvent event) {
		event.enqueueWork(() -> {
			if (CommonConfig.shootProjectile) {
				DispenserBlock.registerBehavior(WAND.get(), (source, stack) -> {
					ServerLevel world = source.level();
					if (!WandItem.isUsable(stack)) {
						return stack;
					}
					Position iposition = DispenserBlock.getDispensePosition(source);
					Direction Direction = source.state().getValue(DispenserBlock.FACING);
					LightEntity entity = new LightEntity(world);
					entity.setPos(iposition.x(), iposition.y(), iposition.z());
					entity.setLightValue(WandItem.getLightValue(stack));
					entity.setColor(WAND.get().getCustomColor(stack).orElse(0));
					entity.shoot(
							Direction.getStepX(),
							Direction.getStepY() + 0.1F,
							Direction.getStepZ(),
							1.3F + world.random.nextFloat() * 0.4F,
							0);
					Vec3 motion = entity.getDeltaMovement();
					entity.setDeltaMovement(motion.add(world.random.nextGaussian() * 0.1D, 0, world.random.nextGaussian() * 0.1D));
					world.addFreshEntity(entity);
					stack.hurtAndBreak(1, world, null, item -> {});
					return stack;
				});
			}
			CauldronInteraction.WATER.map().put(WAND.get(), (blockState, level, blockPos, player, interactionHand, itemStack) -> {
				if (!itemStack.is(ItemTags.DYEABLE)) {
					return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
				} else if (!itemStack.has(DataComponents.DYED_COLOR)) {
					return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
				} else {
					if (!level.isClientSide) {
						itemStack.remove(DataComponents.DYED_COLOR);
						itemStack.update(
								WAND_ITEM_DATA.get(),
								WandItemData.DEFAULT,
								$ -> new WandItemData($.light(), WandItemData.DEFAULT.alpha()));
						player.awardStat(Stats.CLEAN_ARMOR);
						LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
					}

					return ItemInteractionResult.sidedSuccess(level.isClientSide);
				}
			});

			CommonProxy.postRegister();
		});
	}
}
