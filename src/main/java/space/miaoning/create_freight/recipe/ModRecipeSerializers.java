package space.miaoning.create_freight.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import space.miaoning.create_freight.CreateFreight;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CreateFreight.MODID);

    public static final RegistryObject<RecipeSerializer<TradingRecipe>> TRADING = SERIALIZERS.register("trading",TradingRecipe.Serializer::new);
}
