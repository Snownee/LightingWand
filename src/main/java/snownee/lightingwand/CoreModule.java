package snownee.lightingwand;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Categories;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Category;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.loader.event.InitEvent;
import snownee.lightingwand.fabric.FabricWandItem;
import snownee.lightingwand.util.CommonProxy;

@KiwiModule
public class CoreModule extends AbstractModule {

	@NoItem
	public static final KiwiGO<Block> LIGHT = go(() -> new LightBlock(blockProp().noCollission().noLootTable().lightLevel(state -> state.getValue(LightBlock.LIGHT)).sound(SoundType.FROGLIGHT)));

	/* off */
	@Name("light")
	public static final KiwiGO<EntityType<LightEntity>> PROJECTILE = go(() -> FabricEntityTypeBuilder.<LightEntity>create(MobCategory.MISC, LightEntity::new)
			.entityFactory((spawnEntity, world) -> new LightEntity(world))
			//.sized(0.0001F, 0.0001F)
			.fireImmune()
			.trackRangeBlocks(64)
			.trackedUpdateRate(20)
			.forceTrackedVelocityUpdates(true)
			.build()
	);
	/* on */

	@Category(Categories.TOOLS_AND_UTILITIES)
	public static final KiwiGO<Item> WAND = go(() -> new FabricWandItem(itemProp().durability(CommonConfig.wandDurability)));

	public static final KiwiGO<RecipeSerializer<RepairRecipe>> REPAIR = go(RepairRecipe.Serializer::new);

	//	public static boolean psiCompat = Platform.isModLoaded("psi");

	@Override
	protected void init(InitEvent event) {
		//		if (psiCompat) {
		//			PsiCompat.init();
		//		}
		if (CommonConfig.shootProjectile) {
			DispenserBlock.registerBehavior(WAND.get(), (source, stack) -> {
				Level world = source.getLevel();
				if (!world.isClientSide && WandItem.isUsable(stack)) {
					Position iposition = DispenserBlock.getDispensePosition(source);
					Direction Direction = source.getBlockState().getValue(DispenserBlock.FACING);
					LightEntity entity = new LightEntity(world);
					entity.setPos(iposition.x(), iposition.y(), iposition.z());
					entity.lightValue = WandItem.getLightValue(stack);
					entity.shoot(Direction.getStepX(), Direction.getStepY() + 0.1F, Direction.getStepZ(), 1.3F + world.random.nextFloat() * 0.4F, 0);
					Vec3 motion = entity.getDeltaMovement();
					entity.setDeltaMovement(motion.add(world.random.nextGaussian() * 0.1D, 0, world.random.nextGaussian() * 0.1D));
					world.addFreshEntity(entity);
					stack.hurt(1, world.random, null);
				}
				return stack;
			});
		}
		CommonProxy.postRegister(event);
	}
}
