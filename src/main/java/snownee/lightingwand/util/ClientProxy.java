package snownee.lightingwand.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.WandItem;
import snownee.lightingwand.client.EmptyEntityRenderer;
import snownee.lightingwand.compat.ShimmerCompat;

public class ClientProxy {
	public static boolean hasItem() {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return false;
		}
		if (player.isHolding(CoreModule.WAND.get())) {
			return true;
		}
//		if (CommonRegistry.psiCompat) {
//			if (main instanceof ICAD || off instanceof ICAD) {
//				return true;
//			}
//		}
		return false;
	}

	public static void init() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((EntityRenderersEvent.RegisterRenderers event) -> {
			event.registerEntityRenderer(CoreModule.RAW_PROJECTILE, EmptyEntityRenderer::new);
		});
	}

	public static void postRegister() {
		ItemProperties.register(CoreModule.WAND.get(), new ResourceLocation("broken"), (stack, worldIn, entityIn, seed) -> (WandItem.isUsable(stack) ? 0 : 1));
		if (CommonProxy.shimmerCompat)
			ShimmerCompat.init();
	}
}
