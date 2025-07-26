package space.miaoning.create_freight.compat.jei.category;

import com.simibubi.create.AllBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.recipe.TradingRecipe;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TradingRecipeCategory extends AbstractRecipeCategory<TradingRecipe> {
    public static final int width = 82;
    public static final int height = 34;

    public static final RecipeType<TradingRecipe> TYPE = RecipeType.create(
            CreateFreight.MODID, "trading_post", TradingRecipe.class);

    public TradingRecipeCategory(IGuiHelper helper) {
        super(
                TYPE,
                Component.translatable("jei.create_freight.trading_post"),
                helper.createDrawableItemLike(AllBlocks.TABLE_CLOTHS.get(DyeColor.RED)),
                width,
                height);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder pBuilder, TradingRecipe pRecipe, IFocusGroup iFocusGroup) {
        pBuilder.addInputSlot(1, 9)
                .setStandardSlotBackground()
                .addItemStack(pRecipe.getCost());

        pBuilder.addOutputSlot(61, 9)
                .setOutputSlotBackground()
                .addItemStack(pRecipe.getSell())
                .addRichTooltipCallback(((iRecipeSlotView, iTooltipBuilder) -> {
                    int limit = pRecipe.getLimit();
                    iTooltipBuilder.add(limit > 0
                            ? Component.translatable("jei.create_freight.trading_post.limit", limit)
                            : Component.translatable("jei.create_freight.trading_post.unlimited")
                    );

                    String regions = String.join("\n - ", pRecipe.getRegionWeights().keySet());
                    iTooltipBuilder.add(
                            Component.translatable("jei.create_freight.trading_post.regions",
                                    regions.isEmpty() ? "-" : "\n - " + regions)
                    );
                }));
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
