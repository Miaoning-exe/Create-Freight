package space.miaoning.create_freight.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import space.miaoning.create_freight.CreateFreight;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, CreateFreight.MODID);

    public static final RegistryObject<RecipeType<TradingRecipe>> TRADING = RECIPE_TYPES.register("trading",()->registerRecipeType("trading"));

    public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
        return new RecipeType<>()
        {
            public String toString() {
                return CreateFreight.MODID + ":" + identifier;
            }
        };
    }
}
