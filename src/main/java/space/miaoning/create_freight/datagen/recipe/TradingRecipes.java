package space.miaoning.create_freight.datagen.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import space.miaoning.create_freight.datagen.builder.TradingRecipeBuilder;

import java.util.function.Consumer;

public class TradingRecipes {
    public static void register(Consumer<FinishedRecipe> consumer) {
        TradingRecipeBuilder.newRecipe(Items.DIAMOND,3,Items.GOLD_NUGGET,5).build(consumer);
        TradingRecipeBuilder.newRecipe(Items.NETHERRACK,1,Items.GOLD_NUGGET,10).build(consumer);
    }
}
