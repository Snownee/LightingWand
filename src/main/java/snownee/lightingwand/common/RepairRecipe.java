package snownee.lightingwand.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import snownee.lightingwand.CoreModule;

public class RepairRecipe extends CustomRecipe {
	private final String group;
	private final Item repairable;
	private final Ingredient material;
	private final int ratio;

	public RepairRecipe(ResourceLocation Id, String group, Item repairable, Ingredient material, int ratio) {
		super(Id);
		this.group = group;
		this.repairable = repairable;
		this.material = material;
		this.ratio = ratio;
		if (repairable.getMaxDamage() == 0) {
			throw new IllegalArgumentException(String.format("Recipe: %s, Item %s is not repairable", Id, repairable));
		}
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width > 1 || height > 1;
	}

	@Override
	public boolean matches(CraftingContainer inv, Level worldIn) {
		int dust = 0;
		ItemStack wand = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack itemstack = inv.getItem(i);
			if (itemstack.getItem() == repairable && itemstack.getDamageValue() != 0) {
				if (wand.isEmpty()) {
					wand = itemstack;
				} else {
					return false;
				}
			} else if (!itemstack.isEmpty() && material.test(itemstack)) {
				dust++;
			} else if (itemstack != ItemStack.EMPTY) {
				return false;
			}
		}
		return !wand.isEmpty() && dust > 0 && wand.getDamageValue() - Mth.ceil(wand.getMaxDamage() / ratio) * dust > -Mth.ceil(wand.getMaxDamage() / ratio);
	}

	@Override
	public ItemStack assemble(CraftingContainer inv) {
		int dust = 0;
		ItemStack wand = ItemStack.EMPTY;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack itemstack = inv.getItem(i);
			if (itemstack.is(repairable)) {
				wand = itemstack;
			} else if (!itemstack.isEmpty() && material.test(itemstack)) {
				int count = itemstack.getCount();
				if (count > 0) {
					dust++;
				}
			}
		}
		int damage = Mth.clamp(wand.getDamageValue() - Mth.ceil(wand.getMaxDamage() / ratio) * dust, 0, CoreModule.WAND.get().getMaxDamage());
		ItemStack result = wand.copy();
		result.setCount(1);
		result.setDamageValue(damage);
		return result;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CoreModule.REPAIR.get();
	}

	@Override
	public String getGroup() {
		return group;
	}

	public Ingredient getMaterial() {
		return material;
	}

	public Item getRepairable() {
		return repairable;
	}

	public int getRatio() {
		return ratio;
	}

	public static class Serializer implements RecipeSerializer<RepairRecipe> {
		@Override
		public RepairRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			String group = GsonHelper.getAsString(json, "group", "");
			String s = GsonHelper.getAsString(json, "repairable");
			Item repairable = Registry.ITEM.get(new ResourceLocation(s));
			if (repairable == null) {
				throw new JsonSyntaxException("Unknown item '" + s + "'");
			}
			Ingredient material = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "material"));
			int ratio = GsonHelper.getAsInt(json, "ratio");
			return new RepairRecipe(recipeId, group, repairable, material, ratio);
		}

		@Override
		public RepairRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			String group = buffer.readUtf(256);
			Item repairable = Item.byId(buffer.readVarInt());
			Ingredient material = Ingredient.fromNetwork(buffer);
			int ratio = buffer.readVarInt();
			return new RepairRecipe(recipeId, group, repairable, material, ratio);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, RepairRecipe recipe) {
			buffer.writeUtf(recipe.group);
			buffer.writeVarInt(Item.getId(recipe.repairable));
			recipe.material.toNetwork(buffer);
			buffer.writeVarInt(recipe.ratio);
		}

	}
}
