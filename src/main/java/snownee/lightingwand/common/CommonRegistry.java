package snownee.lightingwand.common;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import snownee.lightingwand.Config;
import snownee.lightingwand.LW;

@Mod.EventBusSubscriber()
public class CommonRegistry
{
    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new BlockLight());
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event)
    {
        if (Config.registerWand)
        {
            event.getRegistry().register(new ItemWand());
        }
    }

    @SubscribeEvent
    public static void onEntityRegister(RegistryEvent.Register<EntityEntry> event)
    {
        if (Config.registerWand && Config.shootProjectile)
        {
            event.getRegistry().register(EntityEntryBuilder.<EntityLight>create().entity(EntityLight.class).id(new ResourceLocation(LW.MODID, "light"), 0).name(LW.MODID
                    + ".light").tracker(160, 3, true).build());
        }
    }

    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event)
    {
        if (Config.registerWand)
        {
            event.getRegistry().register(new ShapedOreRecipe(null, ModConstants.WAND, false, new Object[] { " *", "/ ",
                    '/', Items.BLAZE_ROD, '*', "glowstone" }).setRegistryName(LW.MODID, "wand"));
            if (Config.repairRecipe)
            {
                event.getRegistry().register(new RecipeRepair());
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onModelRegister(ModelRegistryEvent event)
    {
        if (Config.registerWand)
        {
            ModelLoader.setCustomModelResourceLocation(ModConstants.WAND, 0, new ModelResourceLocation(ModConstants.WAND.getRegistryName().toString(), "inventory"));
        }
    }
}
