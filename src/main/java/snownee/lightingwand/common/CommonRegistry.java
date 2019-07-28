package snownee.lightingwand.common;

import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import snownee.lightingwand.Config;
import snownee.lightingwand.LW;

@Mod.EventBusSubscriber(bus = Bus.MOD)
public class CommonRegistry
{
    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new LightBlock().setRegistryName(LW.MODID, "light"));
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new WandItem().setRegistryName(LW.MODID, "wand"));
    }

    @SubscribeEvent
    public static void onEntityRegister(RegistryEvent.Register<EntityType<?>> event)
    {
        /* off */
        event.getRegistry().register(EntityType.Builder.create(EntityClassification.MISC)
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
    public static void onRecipeRegister(RegistryEvent.Register<IRecipeSerializer<?>> event)
    {
        event.getRegistry().register(new RepairRecipe.Serializer().setRegistryName(LW.MODID, "repair"));
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event)
    {
        CraftingHelper.register(new ResourceLocation(LW.MODID, "repair_recipe"), (
                json
        ) -> () -> Config.repairRecipe.get());

        if (ModConstants.WAND != Items.AIR)
        {
            if (Config.shootProjectile.get())
            {
                DispenserBlock.registerDispenseBehavior(ModConstants.WAND, new IDispenseItemBehavior()
                {
                    @Override
                    public ItemStack dispense(IBlockSource source, ItemStack stack)
                    {
                        World world = source.getWorld();
                        if (!world.isRemote && WandItem.isUsable(stack))
                        {
                            IPosition iposition = DispenserBlock.getDispensePosition(source);
                            Direction Direction = source.getBlockState().get(DispenserBlock.FACING);
                            LightEntity entity = new LightEntity(world, iposition.getX(), iposition.getY(), iposition.getZ());
                            entity.shoot(Direction.getXOffset(), Direction.getYOffset() + 0.1F, Direction.getZOffset(), 1.3F + world.rand.nextFloat() * 0.4F, 0);
                            Vec3d motion = entity.getMotion();
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
}
