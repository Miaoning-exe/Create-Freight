package space.miaoning.create_freight.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
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

public class TradingRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack sell;
    private final ItemStack cost;
    private final int limit;
    private final NonNullList<BiomeWithWeight> regionWeights;

    public TradingRecipe(ResourceLocation id, ItemStack sell, ItemStack cost, int limit, NonNullList<BiomeWithWeight> regionWeights) {
        this.id = id;
        this.sell = sell;
        this.cost = cost;
        this.limit = limit;
        this.regionWeights = regionWeights;
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

    public ItemStack getCost() {
        return cost.copy();
    }

    public ItemStack getSell() {
        return sell.copy();
    }

    public int getLimit() {
        return limit;
    }

    public NonNullList<BiomeWithWeight> getRegionWeights() {
        return regionWeights;
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

    public static class Serializer implements RecipeSerializer<TradingRecipe> {

        @Override
        public TradingRecipe fromJson(ResourceLocation pId, JsonObject pJson) {
            ItemStack sell = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pJson,"sell"),true);
            ItemStack cost = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pJson,"cost"),true);
            int limit = GsonHelper.getAsInt(pJson,"limit");
            NonNullList<BiomeWithWeight> biomes = NonNullList.create();
            for (JsonElement jsonElement: GsonHelper.getAsJsonArray(pJson,"region_weights")) {
                biomes.add(BiomeWithWeight.deserialize(jsonElement));
            }
            return new TradingRecipe(pId,sell,cost,limit,biomes);
        }

        @Override
        public @Nullable TradingRecipe fromNetwork(ResourceLocation pId, FriendlyByteBuf pBuffer) {
            ItemStack sell = pBuffer.readItem();
            ItemStack cost = pBuffer.readItem();
            int limit = pBuffer.readInt();
            int i = pBuffer.readVarInt();
            NonNullList<BiomeWithWeight> biomes = NonNullList.withSize(i,BiomeWithWeight.EMPTY);

            for(int j=0;j<biomes.size();++j) {
                biomes.set(j,BiomeWithWeight.read(pBuffer));
            }

            return new TradingRecipe(pId,sell,cost,limit,biomes);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, TradingRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.sell);
            pBuffer.writeItem(pRecipe.cost);
            pBuffer.writeInt(pRecipe.limit);
            pBuffer.writeInt(pRecipe.regionWeights.size());

            for (BiomeWithWeight biome : pRecipe.regionWeights) {
                biome.write(pBuffer);
            }
        }
    }
}
