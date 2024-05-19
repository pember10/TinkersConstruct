package slimeknights.tconstruct.library.recipe.casting.material;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Casting recipe that takes an arbitrary fluid of a given amount and set the material on the output based on that fluid
 */
public abstract class AbstractMaterialCastingRecipe extends AbstractCastingRecipe {
  protected static final LoadableField<Integer,AbstractMaterialCastingRecipe> ITEM_COST_FIELD = IntLoadable.FROM_ONE.requiredField("item_cost", r -> r.itemCost);

  @Getter
  private final RecipeSerializer<?> serializer;
  protected final int itemCost;
  @Nullable
  protected MaterialFluidRecipe cachedFluidRecipe = null;

  public AbstractMaterialCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, boolean consumed, boolean switchSlots) {
    super(serializer.getType(), id, group, cast, consumed, switchSlots);
    this.serializer = serializer;
    this.itemCost = itemCost;
  }

  /** Gets the material fluid recipe for the given recipe */
  @Nullable
  protected MaterialFluidRecipe getMaterialFluid(ICastingContainer inv) {
    return MaterialCastingLookup.getCastingFluid(inv);
  }

  /** Gets the cached fluid recipe if it still matches, refetches if not */
  @Nullable
  protected MaterialFluidRecipe getCachedMaterialFluid(ICastingContainer inv) {
    MaterialFluidRecipe fluidRecipe = cachedFluidRecipe;
    if (fluidRecipe == null || !fluidRecipe.matches(inv)) {
      fluidRecipe = getMaterialFluid(inv);
      if (fluidRecipe != null) {
        cachedFluidRecipe = fluidRecipe;
      }
    }
    return fluidRecipe;
  }

  @Override
  public int getCoolingTime(ICastingContainer inv) {
    MaterialFluidRecipe recipe = getCachedMaterialFluid(inv);
    if (recipe != null) {
      return ICastingRecipe.calcCoolingTime(recipe.getTemperature(), recipe.getFluidAmount(inv.getFluid()) * itemCost);
    }
    return 1;
  }

  @Override
  public int getFluidAmount(ICastingContainer inv) {
    MaterialFluidRecipe recipe = getCachedMaterialFluid(inv);
    if (recipe != null) {
      return recipe.getFluidAmount(inv.getFluid()) * itemCost;
    }
    return 1;
  }

  /** Resizes the list of the fluids with respect to the item cost */
  protected List<FluidStack> resizeFluids(List<FluidStack> fluids) {
    if (itemCost != 1) {
      return fluids.stream()
                   .map(fluid -> new FluidStack(fluid, fluid.getAmount() * itemCost))
                   .collect(Collectors.toList());
    }
    return fluids;
  }
}
