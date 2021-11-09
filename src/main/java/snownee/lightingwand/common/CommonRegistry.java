package snownee.lightingwand.common;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.lightingwand.Config;
import snownee.lightingwand.LW;
import snownee.lightingwand.client.EmptyEntityRenderer;

@Mod.EventBusSubscriber(bus = Bus.MOD)
public class CommonRegistry {
	public static EntityType<LightEntity> ENTITY;
	public static boolean psiCompat = false;

	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(new LightBlock().setRegistryName(LW.MODID, "light"));
	}

	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new WandItem().setRegistryName(LW.MODID, "wand"));

		//        if (ModList.get().isLoaded("psi")) {
		//            psiCompat = true;
		//            PsiCompat.init();
		//        }
	}

	@SubscribeEvent
	public static void onEntityRegister(RegistryEvent.Register<EntityType<?>> event) {
		/* off */
        event.getRegistry().register(ENTITY = (EntityType<LightEntity>) EntityType.Builder.of(LightEntity::new, MobCategory.MISC)
                .setCustomClientFactory((
                        spawnEntity, world
                ) -> new LightEntity(world))
                .sized(0.0001F, 0.0001F)
                .fireImmune()
                .setTrackingRange(64)
                .setUpdateInterval(20)
                .setShouldReceiveVelocityUpdates(true)
                .build(LW.MODID + ".light")
                .setRegistryName(LW.MODID, "light"));
        /* on */
	}

	@SubscribeEvent
	public static void onRecipeRegister(RegistryEvent.Register<RecipeSerializer<?>> event) {
		event.getRegistry().register(new RepairRecipe.Serializer().setRegistryName(LW.MODID, "repair"));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		CraftingHelper.register(new RepairRecipeCondition.Serializer());

		if (ModConstants.WAND != Items.AIR) {
			if (Config.shootProjectile.get()) {
				DispenserBlock.registerBehavior(ModConstants.WAND, (source, stack) -> {
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
			ModConstants.WAND.maxDamage = Config.wandDurability.get();
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void clientInit(FMLClientSetupEvent event) {
		ItemProperties.register(ModConstants.WAND, new ResourceLocation("broken"), (stack, worldIn, entityIn, seed) -> (WandItem.isUsable(stack) ? 0 : 1));
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ENTITY, EmptyEntityRenderer::new);
	}
}
