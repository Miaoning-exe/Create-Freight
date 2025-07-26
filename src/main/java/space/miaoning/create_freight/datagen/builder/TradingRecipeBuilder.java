package space.miaoning.create_freight.datagen.builder;

import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.recipe.CFRecipeSerializers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TradingRecipeBuilder {
    private final ItemStack sell;
    private final ItemStack cost;
    private final int limit;
    private final Map<String, Integer> regionWeights = new HashMap<>();

    public TradingRecipeBuilder(ItemStack sell, ItemStack cost, int limit) {
        this.sell = sell;
        this.cost = cost;
        this.limit = limit;
    }

    public static TradingRecipeBuilder newRecipe(ItemLike sell, int count1, ItemLike cost, int count2, int limit) {
        return new TradingRecipeBuilder(new ItemStack(sell, count1), new ItemStack(cost, count2), limit);
    }

    public TradingRecipeBuilder addRegionWithWeight(String regionName, int weight) {
        regionWeights.put(regionName, weight);
        return this;
    }

    public void build(Consumer<FinishedRecipe> consumerIn) {
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(sell.getItem());
        build(consumerIn, CreateFreight.MODID + ":trading_post/"
                + Objects.requireNonNull(location).getPath());
    }

    @SuppressWarnings("removal")
    public void build(Consumer<FinishedRecipe> consumerIn, String save) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(sell.getItem());
        if ((new ResourceLocation(save)).equals(resourcelocation)) {
            throw new IllegalStateException("Trading Post Recipe " + save + " should remove its 'save' argument");
        } else {
            consumerIn.accept(new TradingRecipeBuilder.Result(new ResourceLocation(save), sell, cost, limit, regionWeights));
        }
    }

    @MethodsReturnNonnullByDefault
    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack sell;
        private final ItemStack cost;
        private final int limit;
        private final Map<String, Integer> regionWeights;

        public Result(ResourceLocation id, ItemStack sell, ItemStack cost, int limit, Map<String, Integer> regionWeights) {
            this.id = id;
            this.sell = sell;
            this.cost = cost;
            this.limit = limit;
            this.regionWeights = regionWeights;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            JsonObject objectSell = new JsonObject();
            addItemStack(objectSell, sell);
            pJson.add("sell", objectSell);

            JsonObject objectCost = new JsonObject();
            addItemStack(objectCost, cost);
            pJson.add("cost", objectCost);

            pJson.addProperty("limit", limit);

            JsonObject objectRegionWeights = new JsonObject();
            regionWeights.forEach(objectRegionWeights::addProperty);
            pJson.add("region_weights", objectRegionWeights);
        }

        private void addItemStack(JsonObject pJson, ItemStack stack) {
            pJson.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            if (stack.getCount() > 1) {
                pJson.addProperty("count", stack.getCount());
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return CFRecipeSerializers.TRADING_POST.get();
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
