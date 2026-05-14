package snownee.lightingwand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RepairRecipe extends CustomRecipe {
	private final String group;
	private final ItemStackTemplate repairable;
	private final Ingredient material;
	private final double ratio;

	public RepairRecipe(CraftingBookCategory category, String group, ItemStackTemplate repairable, Ingredient material, double ratio) {
		this.group = group;
		this.repairable = repairable;
		this.material = material;
		this.ratio = ratio;
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		int dust = 0;
		ItemStack wand = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack itemStack = input.getItem(i);
			if (itemStack.is(repairable.item()) && itemStack.getDamageValue() != 0) {
				if (wand.isEmpty()) {
					wand = itemStack;
				} else {
					return false;
				}
			} else if (!itemStack.isEmpty() && material.test(itemStack)) {
				dust++;
			} else if (itemStack != ItemStack.EMPTY) {
				return false;
			}
		}
		return !wand.isEmpty() && dust > 0 && wand.getDamageValue() - Mth.ceil(wand.getMaxDamage() / ratio) * dust > -Mth.ceil(
				wand.getMaxDamage() / ratio);
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		int dust = 0;
		ItemStack wand = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack itemStack = input.getItem(i);
			if (itemStack.is(repairable.item())) {
				wand = itemStack;
			} else if (!itemStack.isEmpty() && material.test(itemStack)) {
				int count = itemStack.getCount();
				if (count > 0) {
					dust++;
				}
			}
		}
		int damage = Mth.clamp(wand.getDamageValue() - Mth.ceil(wand.getMaxDamage() / ratio) * dust, 0, wand.getMaxDamage());
		ItemStack result = wand.copy();
		result.setCount(1);
		result.setDamageValue(damage);
		return result;
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return CoreModule.REPAIR.get();
	}

	@Override
	public String group() {
		return group;
	}

	public Ingredient material() {
		return material;
	}

	public ItemStackTemplate repairable() {
		return repairable;
	}

	public double ratio() {
		return ratio;
	}

	public static final MapCodec<RepairRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(RepairRecipe::category),
			Codec.STRING.optionalFieldOf("group", "").forGetter(RepairRecipe::group),
			ItemStackTemplate.CODEC.fieldOf("repairable").forGetter(RepairRecipe::repairable),
			Ingredient.CODEC.fieldOf("material").forGetter(RepairRecipe::material),
			Codec.DOUBLE.fieldOf("ratio").forGetter(RepairRecipe::ratio)
	).apply(instance, RepairRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RepairRecipe> STREAM_CODEC = StreamCodec.composite(
			CraftingBookCategory.STREAM_CODEC,
			RepairRecipe::category,
			ByteBufCodecs.STRING_UTF8,
			RepairRecipe::group,
			ItemStackTemplate.STREAM_CODEC,
			RepairRecipe::repairable,
			Ingredient.CONTENTS_STREAM_CODEC,
			RepairRecipe::material,
			ByteBufCodecs.DOUBLE,
			RepairRecipe::ratio,
			RepairRecipe::new);
}
