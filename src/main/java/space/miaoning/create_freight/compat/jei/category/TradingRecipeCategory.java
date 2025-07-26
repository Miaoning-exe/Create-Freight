package space.miaoning.create_freight.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.recipe.TradingRecipe;

public class TradingRecipeCategory extends AbstractRecipeCategory<TradingRecipe> {
    public static final int width = 82;
    public static final int height = 34;

    public static final RecipeType<TradingRecipe> TYPE = RecipeType.create(CreateFreight.MODID,"trading", TradingRecipe.class);

    public TradingRecipeCategory(IGuiHelper helper) {
        super(
                TYPE,
                Component.literal("Trading"),
                helper.createDrawableItemLike(Items.EMERALD),
                width,
                height);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder pBuilder, TradingRecipe pRecipe, IFocusGroup iFocusGroup) {
        pBuilder.addInputSlot(1,9)
                .setStandardSlotBackground()
                .addItemStack(pRecipe.getCost());

        pBuilder.addOutputSlot(61,9)
                .setOutputSlotBackground()
                .addItemStack(pRecipe.getSell());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder pBuilder, TradingRecipe pRecipe, IFocusGroup focuses) {
        pBuilder.addRecipeArrow().setPosition(26, 9);
    }

    @Override
    public boolean isHandled(TradingRecipe recipe) {
        return !recipe.isSpecial();
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(TradingRecipe recipe) {
        return recipe.getId();
    }
}
