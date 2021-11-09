package snownee.lightingwand.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class RepairRecipe implements ICraftingRecipe {
	private final ResourceLocation Id;
	private final String group;
	private final Item repairable;
	private final Ingredient material;
	private final int ratio;

	@SuppressWarnings("deprecation")
	public RepairRecipe(ResourceLocation Id, String group, Item repairable, Ingredient material, int ratio) {
		this.Id = Id;
		this.group = group;
		this.repairable = repairable;
		this.material = material;
		this.ratio = ratio;
		if (repairable.getMaxDamage() == 0) {
			throw new IllegalArgumentException(String.format("Recipe: %s, Item %s is not repairable", Id, repairable.getRegistryName()));
		}
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width > 1 || height > 1;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		int dust = 0;
		ItemStack wand = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack.getItem() == repairable && itemstack.getDamage() != 0) {
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
		return !wand.isEmpty() && dust > 0 && wand.getDamage() - MathHelper.ceil(wand.getMaxDamage() / ratio) * dust > -MathHelper.ceil(wand.getMaxDamage() / ratio);
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		int dust = 0;
		ItemStack wand = ItemStack.EMPTY;

		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack.getItem() == repairable) {
				wand = itemstack;
			} else if (!itemstack.isEmpty() && material.test(itemstack)) {
				int count = itemstack.getCount();
				if (count > 0) {
					dust++;
				}
			}
		}
		int damage = MathHelper.clamp(wand.getDamage() - MathHelper.ceil(wand.getMaxDamage() / ratio) * dust, 0, ModConstants.WAND.getMaxDamage(wand));
		ItemStack result = new ItemStack(repairable, 1, wand.getTag());
		result.setDamage(damage);
		return result;
	}

	@Override
	public ResourceLocation getId() {
		return Id;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModConstants.REPAIR;
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

	public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RepairRecipe> {
		@Override
		public RepairRecipe read(ResourceLocation recipeId, JsonObject json) {
			String group = JSONUtils.getString(json, "group", "");
			String s = JSONUtils.getString(json, "repairable");
			Item repairable = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s));
			if (repairable == null) {
				throw new JsonSyntaxException("Unknown item '" + s + "'");
			}
			Ingredient material = Ingredient.deserialize(JSONUtils.getJsonObject(json, "material"));
			int ratio = JSONUtils.getInt(json, "ratio");
			return new RepairRecipe(recipeId, group, repairable, material, ratio);
		}

		@Override
		public RepairRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
			String group = buffer.readString(32767);
			Item repairable = Item.getItemById(buffer.readVarInt());
			Ingredient material = Ingredient.read(buffer);
			int ratio = buffer.readVarInt();
			return new RepairRecipe(recipeId, group, repairable, material, ratio);
		}

		@Override
		public void write(PacketBuffer buffer, RepairRecipe recipe) {
			buffer.writeString(recipe.group);
			buffer.writeVarInt(Item.getIdFromItem(recipe.repairable));
			recipe.material.write(buffer);
			buffer.writeVarInt(recipe.ratio);
		}

	}
}
