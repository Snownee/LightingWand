package snownee.lightingwand.fabric;

import java.util.function.Predicate;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import snownee.lightingwand.CommonConfig;
import snownee.lightingwand.LW;

public class RepairRecipeCondition implements Predicate<JsonObject> {
	public static final ResourceLocation ID = new ResourceLocation(LW.ID, "repair_recipe");
	public static final RepairRecipeCondition INSTANCE = new RepairRecipeCondition();

	private RepairRecipeCondition() {
	}

	@Override
	public boolean test(JsonObject jsonObject) {
		return CommonConfig.repairRecipe;
	}

}
