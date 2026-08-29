package snownee.lightingwand;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class RepairRecipe extends NormalCraftingRecipe {
	private final Ingredient repairable;
	private final Ingredient material;
	private final double ratio;

	public RepairRecipe(
			Recipe.CommonInfo commonInfo,
			CraftingRecipe.CraftingBookInfo bookInfo,
			Ingredient repairable,
			Ingredient material,
			double ratio) {
		super(commonInfo, bookInfo);
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
			if (repairable.test(itemStack) && itemStack.getDamageValue() != 0) {
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
			if (repairable.test(itemStack)) {
				wand = itemStack;
			} else if (!itemStack.isEmpty() && material.test(itemStack)) {
				int count = itemStack.getCount();
				if (count > 0) {
					dust++;
				}
			}
		}
		int damage = Mth.clamp(wand.getDamageValue() - Mth.ceil(wand.getMaxDamage() / ratio) * dust, 0, wand.getMaxDamage());
		ItemStack result = wand.copyWithCount(1);
		result.setDamageValue(damage);
		return result;
	}

	@Override
	public RecipeSerializer<? extends RepairRecipe> getSerializer() {
		return CoreModule.REPAIR.get();
	}

	public Ingredient material() {
		return material;
	}

	public Ingredient repairable() {
		return repairable;
	}

	public double ratio() {
		return ratio;
	}

	@Override
	public List<RecipeDisplay> display() {
		List<SlotDisplay> ingredients = List.of(
				new WithDamageSlotDisplay(repairable().display(), 1.0),
				material().display());
		return List.of(new ShapelessCraftingRecipeDisplay(
				ingredients,
				new WithDamageSlotDisplay(repairable().display(), 1.0 - 1.0 / ratio()),
				new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
	}

	@Override
	protected PlacementInfo createPlacementInfo() {
		return PlacementInfo.create(List.of(repairable, material));
	}

	public static final MapCodec<RepairRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
			CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
			Ingredient.CODEC.fieldOf("repairable").forGetter(RepairRecipe::repairable),
			Ingredient.CODEC.fieldOf("material").forGetter(RepairRecipe::material),
			Codec.DOUBLE.fieldOf("ratio").forGetter(RepairRecipe::ratio)
	).apply(instance, RepairRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RepairRecipe> STREAM_CODEC = StreamCodec.composite(
			Recipe.CommonInfo.STREAM_CODEC,
			o -> o.commonInfo,
			CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
			o -> o.bookInfo,
			Ingredient.CONTENTS_STREAM_CODEC,
			RepairRecipe::repairable,
			Ingredient.CONTENTS_STREAM_CODEC,
			RepairRecipe::material,
			ByteBufCodecs.DOUBLE,
			RepairRecipe::ratio,
			RepairRecipe::new);
}
