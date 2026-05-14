package snownee.lightingwand.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import snownee.lightingwand.CoreModule;
import snownee.lightingwand.compat.ShimmerCompat;

public class ClientProxy {
	public static boolean hasItem() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft == null) {
			return false;
		}
		Player player = minecraft.player;
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

	public static void init(IEventBus modBus) {
		modBus.addListener((EntityRenderersEvent.RegisterRenderers event) -> {
			event.registerEntityRenderer(CoreModule.PROJECTILE.getOrCreate(), NoopRenderer::new);
		});
	}

	public static void postRegister() {
		if (CommonProxy.shimmerCompat) {
			ShimmerCompat.init();
		}
	}
}
