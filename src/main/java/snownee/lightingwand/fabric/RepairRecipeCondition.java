package snownee.lightingwand.fabric;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;
import snownee.lightingwand.CommonConfig;
import snownee.lightingwand.LW;

public class RepairRecipeCondition implements ResourceCondition {
	public static final MapCodec<RepairRecipeCondition> CODEC = MapCodec.unit(new RepairRecipeCondition());
	public static final ResourceConditionType<RepairRecipeCondition> TYPE = ResourceConditionType.create(LW.id("repair_recipe"), CODEC);

	@Override
	public ResourceConditionType<?> getType() {
		return TYPE;
	}

	@Override
	public boolean test(RegistryOps.@Nullable RegistryInfoLookup registryInfo) {
		return CommonConfig.repairRecipe;
	}
}
