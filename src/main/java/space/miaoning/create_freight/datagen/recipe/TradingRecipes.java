package space.miaoning.create_freight.datagen.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.BiomeSources;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import space.miaoning.create_freight.datagen.builder.TradingRecipeBuilder;

import java.util.function.Consumer;

public class TradingRecipes {
    public static void register(Consumer<FinishedRecipe> consumer) {
        TradingRecipeBuilder.newRecipe(Items.DIAMOND,3,Items.GOLD_NUGGET,5,1024)
                .addBiomeWithWeight(Biomes.BEACH,2)
                .addBiomeWithWeight(Biomes.DESERT,3)
                .build(consumer);
        TradingRecipeBuilder.newRecipe(Items.NETHERRACK,1,Items.GOLD_NUGGET,10,1024)
                .addBiomeWithWeight(Biomes.BAMBOO_JUNGLE,1)
                .addBiomeWithWeight(Biomes.SPARSE_JUNGLE,1)
                .addBiomeWithWeight(Biomes.JUNGLE,1)
                .build(consumer);
    }
}
