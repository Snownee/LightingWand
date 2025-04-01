package snownee.lightingwand.neoforge;

import com.mojang.serialization.MapCodec;

import net.neoforged.neoforge.common.conditions.ICondition;
import snownee.lightingwand.CommonConfig;

public class RepairRecipeCondition implements ICondition {
	public static final RepairRecipeCondition INSTANCE = new RepairRecipeCondition();
	public static final MapCodec<RepairRecipeCondition> CODEC = MapCodec.unit(INSTANCE);

	@Override
	public boolean test(IContext context) {
		return CommonConfig.repairRecipe;
	}

	@Override
	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}
}
