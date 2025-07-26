package space.miaoning.create_freight.recipe;

import com.google.gson.JsonObject;
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

    public TradingRecipe(ResourceLocation id, ItemStack sell,ItemStack cost) {
        this.id = id;
        this.sell = sell;
        this.cost = cost;
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
        return CFRecipeSerializers.TRADING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return CFRecipeTypes.TRADING.get();
    }

    public static class Serializer implements RecipeSerializer<TradingRecipe> {

        @Override
        public TradingRecipe fromJson(ResourceLocation pId, JsonObject pJson) {
            ItemStack sell = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pJson,"sell"),true);
            ItemStack cost = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pJson,"cost"),true);

            return new TradingRecipe(pId,sell,cost);
        }

        @Override
        public @Nullable TradingRecipe fromNetwork(ResourceLocation pId, FriendlyByteBuf pBuffer) {
            ItemStack sell = pBuffer.readItem();
            ItemStack cost = pBuffer.readItem();

            return new TradingRecipe(pId,sell,cost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, TradingRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.sell);
            pBuffer.writeItem(pRecipe.cost);
        }
    }
}
