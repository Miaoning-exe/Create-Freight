package space.miaoning.create_freight.core;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import space.miaoning.create_freight.recipe.TradingRecipe;

import java.util.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TradingManager {
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

    public static List<TradingRecipe> getRandomTradingRecipes(String regionName, int count) {
        List<TradingRecipe> availableRecipes = tradingRecipes.stream()
                .filter(recipe -> recipe.getRegionWeights().containsKey(regionName))
                .toList();

        if (availableRecipes.size() <= count) {
            return new ArrayList<>(availableRecipes);
        }

        List<TradingRecipe> weightedRecipes = new ArrayList<>();
        for (TradingRecipe recipe : availableRecipes) {
            int weight = recipe.getRegionWeights().get(regionName);
            for (int i = 0; i < weight; i++) {
                weightedRecipes.add(recipe);
            }
        }

        Set<TradingRecipe> selectedRecipes = new HashSet<>();
        Random random = new Random();

        while (selectedRecipes.size() < count) {
            selectedRecipes.add(weightedRecipes.get(random.nextInt(weightedRecipes.size())));
        }

        return new ArrayList<>(selectedRecipes);
    }
}
