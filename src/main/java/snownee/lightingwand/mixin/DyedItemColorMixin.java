package snownee.lightingwand.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import snownee.lightingwand.CoreModule;

@Mixin(DyedItemColor.class)
public class DyedItemColorMixin {
	@WrapOperation(method = "applyDyes", at = @At(value = "NEW", target = "(IZ)Lnet/minecraft/world/item/component/DyedItemColor;"))
	private static DyedItemColor applyDyes(
			int color,
			boolean showInTooltip,
			Operation<DyedItemColor> original,
			@Local(argsOnly = true) ItemStack itemStack,
			@Local DyedItemColor dyedItemColor) {
		if (dyedItemColor == null && itemStack.has(CoreModule.WAND_ITEM_DATA.get())) {
			return original.call(color, false);
		}
		return original.call(color, showInTooltip);
	}
}
