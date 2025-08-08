package space.miaoning.create_freight.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import space.miaoning.create_freight.recipe.CFRecipeTypes;
import space.miaoning.create_freight.recipe.TradingRecipe;

import java.util.ArrayList;
import java.util.List;

public class TradingRecipeHelper {
    public static List<TradingRecipe> getRandomTradingRecipes(Level level, String regionName, int count, RandomSource random) {
        List<TradingRecipe> availableRecipes = level.getRecipeManager()
                .getAllRecipesFor(CFRecipeTypes.TRADING_POST.get())
                .stream()
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
