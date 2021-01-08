package snownee.lightingwand.common;

import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.lightingwand.Config;
import snownee.lightingwand.LW;
import snownee.lightingwand.client.EmptyEntityRenderer;
import snownee.lightingwand.compat.PsiCompat;

@Mod.EventBusSubscriber(bus = Bus.MOD)
public class CommonRegistry {
    private static EntityType<LightEntity> ENTITY;
    public static boolean psiCompat = false;

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new LightBlock().setRegistryName(LW.MODID, "light"));
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new WandItem().setRegistryName(LW.MODID, "wand"));

        if (ModList.get().isLoaded("psi")) {
            psiCompat = true;
            PsiCompat.init();
        }
    }

    @SubscribeEvent
    public static void onEntityRegister(RegistryEvent.Register<EntityType<?>> event) {
        /* off */
        event.getRegistry().register(ENTITY = (EntityType<LightEntity>) EntityType.Builder.create(EntityClassification.MISC)
                .setCustomClientFactory((
                        spawnEntity, world
                ) -> new LightEntity(world))
                .size(0.0001F, 0.0001F)
                .immuneToFire()
                .setTrackingRange(64)
                .setUpdateInterval(20)
                .setShouldReceiveVelocityUpdates(true)
                .build(LW.MODID + ".light")
                .setRegistryName(LW.MODID, "light"));
        /* on */
    }

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        event.getRegistry().register(new RepairRecipe.Serializer().setRegistryName(LW.MODID, "repair"));
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        CraftingHelper.register(new RepairRecipeCondition.Serializer());

        if (ModConstants.WAND != Items.AIR) {
            if (Config.shootProjectile.get()) {
                DispenserBlock.registerDispenseBehavior(ModConstants.WAND, new IDispenseItemBehavior() {
                    @Override
                    public ItemStack dispense(IBlockSource source, ItemStack stack) {
                        World world = source.getWorld();
                        if (!world.isRemote && WandItem.isUsable(stack)) {
                            IPosition iposition = DispenserBlock.getDispensePosition(source);
                            Direction Direction = source.getBlockState().get(DispenserBlock.FACING);
                            LightEntity entity = new LightEntity(world, iposition.getX(), iposition.getY(), iposition.getZ());
                            entity.shoot(Direction.getXOffset(), Direction.getYOffset() + 0.1F, Direction.getZOffset(), 1.3F + world.rand.nextFloat() * 0.4F, 0);
                            Vector3d motion = entity.getMotion();
                            entity.setMotion(motion.add(world.rand.nextGaussian() * 0.1D, 0, world.rand.nextGaussian() * 0.1D));
                            world.addEntity(entity);
                            stack.attemptDamageItem(1, world.rand, null);
                        }
                        return stack;
                    }
                });
            }
            ModConstants.WAND.maxDamage = Config.wandDurability.get();
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void clientInit(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ENTITY, EmptyEntityRenderer::new);

        ItemModelsProperties.func_239418_a_(ModConstants.WAND, new ResourceLocation("broken"), (stack, worldIn, entityIn) -> {
            return WandItem.isUsable(stack) ? 0 : 1;
        });
    }
}
