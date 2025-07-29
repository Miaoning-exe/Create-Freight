package space.miaoning.create_freight.compat.jei.category;

import com.simibubi.create.AllBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.recipe.TradingRecipe;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("removal")
@ParametersAreNonnullByDefault
public class TradingRecipeCategory extends AbstractRecipeCategory<TradingRecipe> {
    public static final int width = 82;
    public static final int height = 40;

    public static final RecipeType<TradingRecipe> TYPE = RecipeType.create(CreateFreight.MODID, "trading_post", TradingRecipe.class);

    private final IDrawable limit_icon;
    private final IDrawable region_weights_icon;

    public TradingRecipeCategory(IGuiHelper helper) {
        super(
                TYPE,
                Component.translatable("jei.create_freight.trading_post"),
                helper.createDrawableItemLike(AllBlocks.TABLE_CLOTHS.get(DyeColor.RED)),
                width,
                height);
        limit_icon = helper.createDrawable(new ResourceLocation("create_freight:textures/gui/icons.png"), 16, 0, 16, 16);
        region_weights_icon = helper.createDrawable(new ResourceLocation("create_freight:textures/gui/icons.png"), 0, 0, 16, 16);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder pBuilder, TradingRecipe pRecipe, IFocusGroup iFocusGroup) {
        pBuilder.addInputSlot(1, 12)
                .setStandardSlotBackground()
                .addItemStack(pRecipe.getCost());

        pBuilder.addOutputSlot(61, 12)
                .setOutputSlotBackground()
                .addItemStack(pRecipe.getSell());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder pBuilder, TradingRecipe pRecipe, IFocusGroup focuses) {
        pBuilder.addRecipeArrow().setPosition(26, 12);
    }

    @Override
    public void draw(TradingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        limit_icon.draw(guiGraphics, 25, 0);
        region_weights_icon.draw(guiGraphics, 25, 24);
    }

    @Override
    public boolean isHandled(TradingRecipe recipe) {
        return !recipe.isSpecial();
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(TradingRecipe recipe) {
        return recipe.getId();
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, TradingRecipe pRecipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (isCursorInsideBounds(25, 0, 16, 16, mouseX, mouseY)) {
            int limit = pRecipe.getLimit();
            tooltip.add(limit > 0
                    ? Component.translatable("jei.create_freight.trading_post.limit", limit)
                    : Component.translatable("jei.create_freight.trading_post.unlimited"));
        }
        if (isCursorInsideBounds(25, 24, 16, 16, mouseX, mouseY)) {
            String regions = String.join(", ", pRecipe.getRegionWeights().keySet());
            tooltip.add(Component.translatable("jei.create_freight.trading_post.regions",
                        regions.isEmpty() ? "-" : regions));
        }


    }

    private static boolean isCursorInsideBounds(int iconX, int iconY, int iconWidth, int iconHeight, double cursorX, double cursorY) {
        return iconX <= cursorX && cursorX < iconX + iconWidth && iconY <= cursorY && cursorY < iconY + iconHeight;
    }
}
