package snownee.lightingwand.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
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

	public static void init(IEventBus modBus) {
		modBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
			event.registerEntityRenderer(CoreModule.PROJECTILE.getOrCreate(), EmptyEntityRenderer::new);
		});
	}

	public static void postRegister() {
		ItemProperties.register(
				CoreModule.WAND.get(),
				ResourceLocation.withDefaultNamespace("broken"),
				(stack, worldIn, entityIn, seed) -> (WandItem.isUsable(stack) ? 0 : 1));
		if (CommonProxy.shimmerCompat) {
			ShimmerCompat.init();
		}
	}
}
