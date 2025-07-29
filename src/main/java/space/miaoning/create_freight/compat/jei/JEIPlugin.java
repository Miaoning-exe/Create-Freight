package space.miaoning.create_freight.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.compat.jei.category.TradingRecipeCategory;
import space.miaoning.create_freight.recipe.CFRecipeTypes;

@JeiPlugin
@SuppressWarnings("removal")
@MethodsReturnNonnullByDefault
public class JEIPlugin implements IModPlugin {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CreateFreight.MODID,"jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new TradingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

        registration.addRecipes(TradingRecipeCategory.TYPE,manager.getAllRecipesFor(CFRecipeTypes.TRADING_POST.get()));
    }
}
