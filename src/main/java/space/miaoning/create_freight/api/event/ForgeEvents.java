package space.miaoning.create_freight.api.event;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.config.TradingConfig;
import space.miaoning.create_freight.datagen.recipe.TradingRecipes;

import static net.minecraftforge.common.brewing.BrewingRecipeRegistry.addRecipe;

@Mod.EventBusSubscriber(modid = CreateFreight.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvents {
    /**
     * 监听配置文件重载事件。
     * 当玩家使用 /reload 命令或者在配置界面修改了服务端配置时触发。
     */
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent.Reloading event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == TradingConfig.SPEC) {
            TradingConfig.parseRecipes();
        }
    }
}
