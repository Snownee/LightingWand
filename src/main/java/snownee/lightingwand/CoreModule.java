package snownee.lightingwand;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.KiwiModule.Name;
import snownee.kiwi.KiwiModule.NoItem;
import snownee.kiwi.KiwiModule.Subscriber.Bus;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.loader.event.ClientInitEvent;
import snownee.kiwi.loader.event.InitEvent;
import snownee.lightingwand.client.EmptyEntityRenderer;
import snownee.lightingwand.common.LightBlock;
import snownee.lightingwand.common.LightEntity;
import snownee.lightingwand.common.RepairRecipe;
import snownee.lightingwand.common.RepairRecipeCondition;
import snownee.lightingwand.common.WandItem;

@KiwiModule
@KiwiModule.Subscriber(Bus.MOD)
public class CoreModule extends AbstractModule {

	@NoItem
	public static Block LIGHT = new LightBlock(blockProp(Blocks.AIR).lightLevel(state -> state.getValue(LightBlock.LIGHT)).sound(SoundType.SLIME_BLOCK));

	/* off */
	@Name("light")
	public static EntityType<LightEntity> PROJECTILE = EntityType.Builder.<LightEntity>of(LightEntity::new, MobCategory.MISC)
			.setCustomClientFactory((spawnEntity, world) -> new LightEntity(world))
			.sized(0.0001F, 0.0001F)
			.fireImmune()
			.setTrackingRange(64)
			.setUpdateInterval(20)
			.setShouldReceiveVelocityUpdates(true)
			.build(LW.MODID + ".light");
	/* on */

	public static Item WAND = new WandItem(itemProp().tab(CreativeModeTab.TAB_TOOLS).setNoRepair().durability(CommonConfig.wandDurability));

	public static RecipeSerializer<?> REPAIR = new RepairRecipe.Serializer();

	public static boolean psiCompat = Platform.isModLoaded("psi");

	@Override
	protected void init(InitEvent event) {
		//		if (psiCompat) {
		//			PsiCompat.init();
		//		}
		CraftingHelper.register(new RepairRecipeCondition.Serializer());
		if (CommonConfig.shootProjectile) {
			DispenserBlock.registerBehavior(WAND, (source, stack) -> {
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
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void clientInit(ClientInitEvent event) {
		ItemProperties.register(WAND, new ResourceLocation("broken"), (stack, worldIn, entityIn, seed) -> (WandItem.isUsable(stack) ? 0 : 1));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(PROJECTILE, EmptyEntityRenderer::new);
	}
}
