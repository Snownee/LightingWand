package snownee.lightingwand.common;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import snownee.lightingwand.CommonConfig;
import snownee.lightingwand.LW;

public class RepairRecipeCondition implements ICondition {
	private static final ResourceLocation NAME = new ResourceLocation(LW.MODID, "repair_recipe");
	private static final RepairRecipeCondition INSTANCE = new RepairRecipeCondition();

	private RepairRecipeCondition() {
	}

	@Override
	public ResourceLocation getID() {
		return NAME;
	}

	@Override
	public boolean test() {
		return CommonConfig.repairRecipe;
	}

	public static class Serializer implements IConditionSerializer<RepairRecipeCondition> {

		@Override
		public void write(JsonObject json, RepairRecipeCondition value) {
		}

		@Override
		public RepairRecipeCondition read(JsonObject json) {
			return INSTANCE;
		}

		@Override
		public ResourceLocation getID() {
			return NAME;
		}

	}

}
