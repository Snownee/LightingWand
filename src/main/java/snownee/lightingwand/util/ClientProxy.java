package snownee.lightingwand.util;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
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

	public static void postRegister() {
		ItemProperties.register(CoreModule.WAND.get(), new ResourceLocation("broken"), (stack, worldIn, entityIn, seed) -> (WandItem.isUsable(stack) ? 0 : 1));
		EntityRendererRegistry.register(CoreModule.PROJECTILE.get(), EmptyEntityRenderer::new);
		if (CommonProxy.shimmerCompat)
			ShimmerCompat.init();
	}
}
