package space.miaoning.create_freight.config;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.recipe.TradingRecipe;

import java.util.*;

@Mod.EventBusSubscriber(modid = CreateFreight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TradingConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TRADING_RECIPES;

    //定义配方的配置格式
    static {
        BUILDER.push("trading");

        TRADING_RECIPES = BUILDER
                .comment(
                        "在这里定义交易站的配方",
                        "配方格式: \"出售的物品|消耗的物品|交易上限|生物群系1,权重1;生物群系2,权重2;...\"",
                        "物品格式（支持NBT）：\"数量<空格>物品ID{NBT数据}\"",
                        "示例1: \"1 minecraft:coal|32 minecraft:diamond|1024|minecraft:desert,2;minecraft:plain,3\"",
                        "示例2: \"1 minecraft:diamond_sword{Enchantments:[{id:\\\"minecraft:sharpness\\\",lvl:5s}]}|32 minecraft:diamond|100|minecraft:desert,2;minecraft:plain,3\""
                )
                .defineList(
                        "tradingRecipes",
                        List.of(),   // 默认值
                        obj -> obj instanceof String
                );
        BUILDER.pop();
        SPEC = BUILDER.build();
    }


    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TradingConfig.SPEC, "create_freight-server.toml");
    }
}
