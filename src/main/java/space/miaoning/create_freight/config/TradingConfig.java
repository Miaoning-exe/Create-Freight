package space.miaoning.create_freight.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.util.ExecutionChecker;

import java.util.List;

@Mod.EventBusSubscriber(modid = CreateFreight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TradingConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TRADING_RECIPES;
    public static final ForgeConfigSpec.ConfigValue<String> TRADING_REFRESH_FREQUENCY;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> TRADING_REFRESH_HOURS;
    public static final ForgeConfigSpec.ConfigValue<String> TRADING_RESTOCK_FREQUENCY;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> TRADING_RESTOCK_HOURS;

    //定义配方的配置格式
    static {
        BUILDER.push("trading");

        TRADING_RECIPES = BUILDER
                .comment(
                        "在这里定义交易站的配方",
                        "配方格式: \"出售的物品|消耗的物品|交易上限|生物群系1,权重1;生物群系2,权重2;...\"",
                        "物品格式（支持NBT）：\"数量<空格>物品ID{NBT数据}\"",
                        "示例1: \"1 minecraft:coal|32 minecraft:diamond|1024|desert,2;plain,3\"",
                        "示例2: \"1 minecraft:diamond_sword{Enchantments:[{id:\\\"minecraft:sharpness\\\",lvl:5s}]}|32 minecraft:diamond|100|desert,2;plain,3\""
                )
                .defineList(
                        "tradingRecipes",
                        List.of(),   // 默认值
                        obj -> obj instanceof String
                );

        TRADING_REFRESH_FREQUENCY = BUILDER
                .comment(
                        "配方刷新频率类型: daily, weekly, monthly",
                        "daily: 每天为基准",
                        "weekly: 每周为基准（周一00:00为基准点）",
                        "monthly: 每月为基准（1号00:00为基准点）"
                )
                .define("refreshFrequency", "weekly");

        TRADING_REFRESH_HOURS = BUILDER
                .comment(
                        "配方刷新时间点（相对基准点的小时偏移）",
                        "例如: [0] 表示基准点执行",
                        "例如: [24, 96] 表示weekly模式下周二00:00和周四00:00执行",
                        "例如: [8, 20] 表示daily模式下每天08:00和20:00执行"
                )
                .defineList(
                        "refreshHours",
                        List.of(0),
                        obj -> obj instanceof Integer
                );

        TRADING_RESTOCK_FREQUENCY = BUILDER
                .comment(
                        "补货频率类型: daily, weekly, monthly",
                        "daily: 每天为基准",
                        "weekly: 每周为基准（周一00:00为基准点）",
                        "monthly: 每月为基准（1号00:00为基准点）"
                )
                .define("restockFrequency", "daily");

        TRADING_RESTOCK_HOURS = BUILDER
                .comment(
                        "补货时间点（相对基准点的小时偏移）",
                        "例如: [0] 表示每天00:00执行",
                        "例如: [6, 18] 表示每天06:00和18:00执行"
                )
                .defineList(
                        "restockHours",
                        List.of(0),
                        obj -> obj instanceof Integer
                );

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static ExecutionChecker tradingRefreshChecker;
    public static ExecutionChecker tradingRestockChecker;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        tradingRefreshChecker = new ExecutionChecker(
                TRADING_REFRESH_FREQUENCY.get(),
                TRADING_REFRESH_HOURS.get()
        );
        tradingRestockChecker = new ExecutionChecker(
                TRADING_RESTOCK_FREQUENCY.get(),
                TRADING_RESTOCK_HOURS.get()
        );
    }
}
