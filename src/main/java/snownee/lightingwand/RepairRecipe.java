package snownee.lightingwand;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class RepairRecipe extends CustomRecipe {
	private final String group;
	private final Item repairable;
	private final Ingredient material;
	private final double ratio;

	public RepairRecipe(CraftingBookCategory category, String group, Item repairable, Ingredient material, double ratio) {
		super(category);
		this.group = group;
		this.repairable = repairable;
		this.material = material;
		this.ratio = ratio;
		if (!repairable.components().has(DataComponents.MAX_DAMAGE)) {
			throw new IllegalArgumentException(String.format("Item %s is not repairable", repairable));
		}
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width > 1 || height > 1;
	}

	@Override
	public boolean matches(CraftingInput input, Level worldIn) {
		int dust = 0;
		ItemStack wand = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack itemstack = input.getItem(i);
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
		return !wand.isEmpty() && dust > 0 && wand.getDamageValue() - Mth.ceil(wand.getMaxDamage() / ratio) * dust > -Mth.ceil(
				wand.getMaxDamage() / ratio);
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider) {
		int dust = 0;
		ItemStack wand = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); ++i) {
			ItemStack itemstack = input.getItem(i);
			if (itemstack.is(repairable)) {
				wand = itemstack;
			} else if (!itemstack.isEmpty() && material.test(itemstack)) {
				int count = itemstack.getCount();
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
	public RecipeSerializer<?> getSerializer() {
		return CoreModule.REPAIR.get();
	}

	@Override
	public String getGroup() {
		return group;
	}

	public Ingredient material() {
		return material;
	}

	public Item repairable() {
		return repairable;
	}

	public double ratio() {
		return ratio;
	}

	public static class Serializer implements RecipeSerializer<RepairRecipe> {
		public static final MapCodec<RepairRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(RepairRecipe::category),
				Codec.STRING.optionalFieldOf("group", "").forGetter(RepairRecipe::getGroup),
				BuiltInRegistries.ITEM.byNameCodec().fieldOf("repairable").forGetter(RepairRecipe::repairable),
				Ingredient.CODEC_NONEMPTY.fieldOf("material").forGetter(RepairRecipe::material),
				Codec.DOUBLE.fieldOf("ratio").forGetter(RepairRecipe::ratio)
		).apply(instance, RepairRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, RepairRecipe> STREAM_CODEC = StreamCodec.composite(
				CraftingBookCategory.STREAM_CODEC,
				RepairRecipe::category,
				ByteBufCodecs.STRING_UTF8,
				RepairRecipe::getGroup,
				ByteBufCodecs.registry(Registries.ITEM),
				RepairRecipe::repairable,
				Ingredient.CONTENTS_STREAM_CODEC,
				RepairRecipe::material,
				ByteBufCodecs.DOUBLE,
				RepairRecipe::ratio,
				RepairRecipe::new);

		@Override
		public MapCodec<RepairRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, RepairRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
