package space.miaoning.create_freight.datagen.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.recipe.BiomeWithWeight;
import space.miaoning.create_freight.recipe.CFRecipeSerializers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TradingRecipeBuilder {
    private final ItemStack sell;
    private final ItemStack cost;
    private final int limit;
    private final List<BiomeWithWeight> regionWeights = new ArrayList<>();

    public TradingRecipeBuilder(ItemStack sell,ItemStack cost,int limit,List<BiomeWithWeight> regionWeights) {
        this.sell = sell;
        this.cost = cost;
        this.limit = limit;
        this.regionWeights.addAll(regionWeights);
    }

    public TradingRecipeBuilder(ItemStack sell,ItemStack cost,int limit) {
        this.sell = sell;
        this.cost = cost;
        this.limit = limit;
    }

    public static TradingRecipeBuilder newRecipe(ItemLike sell,int count1,ItemLike cost,int count2,int limit) {
        return new TradingRecipeBuilder(new ItemStack(sell,count1),new ItemStack(cost,count2),limit);
    }

    public TradingRecipeBuilder addBiomeWithWeight(ResourceKey<Biome> biomeKey, int weight) {
        BiomeWithWeight biomeW = new BiomeWithWeight(biomeKey.location(),weight);
        regionWeights.add(biomeW);
        return this;
    }

    public void build(Consumer<FinishedRecipe> consumerIn) {
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(sell.getItem());
        build(consumerIn, CreateFreight.MODID + ":trading_post/" + location.getPath());
    }

    public void build(Consumer<FinishedRecipe> consumerIn, String save) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(sell.getItem());
        if ((new ResourceLocation(save)).equals(resourcelocation)) {
            throw new IllegalStateException("Trading Post Recipe " + save + " should remove its 'save' argument");
        } else {
            consumerIn.accept(new TradingRecipeBuilder.Result(new ResourceLocation(save),sell,cost,limit, regionWeights));
        }
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack sell;
        private final ItemStack cost;
        private final int limit;
        private final List<BiomeWithWeight> biomes;

        public Result(ResourceLocation id, ItemStack sell, ItemStack cost, int limit,List<BiomeWithWeight> biomes) {
            this.id = id;
            this.sell = sell;
            this.cost = cost;
            this.limit = limit;
            this.biomes = biomes;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            JsonObject objectSell = new JsonObject();
            addItemStack(objectSell,sell);
            pJson.add("sell",objectSell);

            JsonObject objectCost = new JsonObject();
            addItemStack(objectCost,cost);
            pJson.add("cost",objectCost);

            pJson.addProperty("limit",limit);

            JsonArray jsonArray = new JsonArray();
            for (BiomeWithWeight biome: biomes) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("biome", biome.getBiome().toString());
                jsonObject.addProperty("weight",biome.getWeight());
                jsonArray.add(jsonObject);
            }
            pJson.add("region_weights",jsonArray);
        }

        private void addItemStack(JsonObject pJson,ItemStack stack) {
            pJson.addProperty("item",ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
            if (stack.getCount() > 1) {
                pJson.addProperty("count",stack.getCount());
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

        private void test(JsonObject pJson) {
            //pJson.addProperty("1",Biomes.BEACH.toString()); -> "ResourceKey[minecraft:worldgen/biome / minecraft:beach]"
            //pJson.addProperty("3",Biomes.BEACH.registry().toString()); -> "minecraft:worldgen/biome"

            ResourceLocation id = Biomes.BEACH.location();

            pJson.addProperty("2",id.toString());// -> "minecraft:beach"

//            pJson.addProperty("4",
//                ForgeRegistries.BIOMES.getValue(id).toString()
//            );
        }
    }
}
