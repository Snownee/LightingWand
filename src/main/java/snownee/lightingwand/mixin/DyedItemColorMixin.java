package snownee.lightingwand.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import snownee.lightingwand.CoreModule;

@Mixin(DyedItemColor.class)
public class DyedItemColorMixin {
	@WrapMethod(method = "addToTooltip")
	private void lightingwand$avoidTooltip(
			Item.TooltipContext context,
			Consumer<Component> consumer,
			TooltipFlag flag,
			DataComponentGetter components,
			Operation<Void> original) {
		if (components.has(CoreModule.WAND_ITEM_DATA.get())) {
			return;
		}
		original.call(context, consumer, flag, components);
	}
}
