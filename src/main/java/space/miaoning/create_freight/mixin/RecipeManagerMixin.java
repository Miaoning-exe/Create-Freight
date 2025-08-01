package space.miaoning.create_freight.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerMixin {

    /**
     * 返回 'recipes' 字段的公共方法。
     */
    @Accessor("recipes")
    Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> getRecipes();

    /**
     * 修改 'recipes' 字段的公共方法。
     */
    @Accessor("recipes")
    void setRecipes(Map<RecipeType<?>, ? extends Map<ResourceLocation, Recipe<?>>> recipes);

    /**
     * 返回 'recipes' 字段的公共方法。
     */
    @Accessor("byName")
    Map<ResourceLocation, Recipe<?>> getByName();

    /**
     * 修改 'recipes' 字段的公共方法。
     */
    @Accessor("byName")
    void setByName(Map<ResourceLocation, Recipe<?>> byName);
}