package space.miaoning.create_freight.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TradingRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack sell;
    private final ItemStack cost;
    private final int limit;
    private final Map<String, Integer> region_weights;

    public TradingRecipe(ResourceLocation id,
                         ItemStack sell, ItemStack cost,
                         int limit,
                         Map<String, Integer> region_weights) {
        this.id = id;
        this.sell = sell;
        this.cost = cost;
        this.limit = limit;
        this.region_weights = region_weights;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        return pContainer.getItem(0).is(cost.getItem());
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer, RegistryAccess registryAccess) {
        return sell.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return sell.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CFRecipeSerializers.TRADING_POST.get();
    }

    @Override
    public RecipeType<?> getType() {
        return CFRecipeTypes.TRADING_POST.get();
    }

    public ItemStack getSell() {
        return sell.copy();
    }

    public ItemStack getCost() {
        return cost.copy();
    }

    public int getLimit() {
        return limit;
    }

    public Map<String, Integer> getRegionWeights() {
        return region_weights;
    }

    public static class Serializer implements RecipeSerializer<TradingRecipe> {

        @Override
        public TradingRecipe fromJson(ResourceLocation pId, JsonObject pJson) {
            ItemStack sell = CraftingHelper.getItemStack(
                    GsonHelper.getAsJsonObject(pJson, "sell"), true);
            ItemStack cost = CraftingHelper.getItemStack(
                    GsonHelper.getAsJsonObject(pJson, "cost"), true);
            int limit = GsonHelper.getAsInt(pJson, "limit", -1);

            Map<String, Integer> region_weights = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry :
                    GsonHelper.getAsJsonObject(pJson, "region_weights").entrySet()) {
                region_weights.put(entry.getKey(), entry.getValue().getAsInt());
            }

            return new TradingRecipe(pId, sell, cost, limit, region_weights);
        }

        @Override
        public @Nullable TradingRecipe fromNetwork(ResourceLocation pId, FriendlyByteBuf pBuffer) {
            ItemStack sell = pBuffer.readItem();
            ItemStack cost = pBuffer.readItem();
            int limit = pBuffer.readVarInt();

            Map<String, Integer> region_weights = pBuffer.readMap(
                    FriendlyByteBuf::readUtf, FriendlyByteBuf::readVarInt);

            return new TradingRecipe(pId, sell, cost, limit, region_weights);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, TradingRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.sell);
            pBuffer.writeItem(pRecipe.cost);
            pBuffer.writeVarInt(pRecipe.limit);
            pBuffer.writeMap(
                    pRecipe.region_weights, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeVarInt);
        }
    }
}
