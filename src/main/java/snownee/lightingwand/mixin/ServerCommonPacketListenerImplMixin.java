package snownee.lightingwand.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import snownee.lightingwand.WandItem;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {
	@SuppressWarnings("ConstantValue")
	@Inject(method = "handleCustomClickAction", at = @At("HEAD"))
	private void handleCustomClickAction(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
		if ((Object) this instanceof ServerGamePacketListenerImpl listener && packet.payload().isPresent() &&
				WandItem.LIGHT_DIALOG_ACTION.equals(packet.id())) {
			WandItem.handleLightDialogAction(listener.player, packet.payload().get());
		}
	}
}
