package snownee.lightingwand.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.player.Player;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.compat.ShimmerCompat;

public class ClientProxy {
	public static boolean hasItem() {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return false;
		}
		return player.isHolding(CoreModule.WAND.get());
//		if (CommonRegistry.psiCompat) {
//			if (main instanceof ICAD || off instanceof ICAD) {
//				return true;
//			}
//		}
	}

	public static void postRegister() {
		EntityRenderers.register(CoreModule.PROJECTILE.get(), NoopRenderer::new);
		if (CommonProxy.shimmerCompat) {
			ShimmerCompat.init();
		}
	}
}
