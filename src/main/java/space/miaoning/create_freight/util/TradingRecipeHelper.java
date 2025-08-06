package space.miaoning.create_freight.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import space.miaoning.create_freight.recipe.TradingRecipe;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TradingRecipeHelper {
    private static final List<TradingRecipe> tradingRecipes = new ArrayList<>();

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        RecipeManager recipeManager = event.getRecipeManager();

        tradingRecipes.clear();

        for (Recipe<?> recipe : recipeManager.getRecipes()) {
            if (recipe instanceof TradingRecipe tRecipe) {
                tradingRecipes.add(tRecipe);
            }
        }
    }

    public static List<TradingRecipe> getRandomTradingRecipes(String regionName, int count, RandomSource random) {
        List<TradingRecipe> availableRecipes = tradingRecipes.stream()
                .filter(recipe -> recipe.getRegionWeights().getOrDefault(regionName, 0) > 0)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        if (availableRecipes.size() <= count) {
            return availableRecipes;
        }

        List<TradingRecipe> result = new ArrayList<>();

        int totalWeight = availableRecipes.stream()
                .mapToInt(recipe -> recipe.getRegionWeights().get(regionName))
                .sum();

        for (int i = 0; i < count; i++) {
            int randomWeight = random.nextInt(totalWeight);
            int currentWeight = 0;

            for (int j = 0; j < availableRecipes.size(); j++) {
                TradingRecipe recipe = availableRecipes.get(j);
                int recipeWeight = recipe.getRegionWeights().get(regionName);
                currentWeight += recipeWeight;

                if (randomWeight < currentWeight) {
                    result.add(recipe);
                    availableRecipes.remove(j);
                    totalWeight -= recipeWeight;
                    break;
                }
            }
        }

        return result;
    }
}
