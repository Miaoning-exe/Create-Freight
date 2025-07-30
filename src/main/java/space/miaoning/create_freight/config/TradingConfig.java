package space.miaoning.create_freight.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.recipe.TradingRecipe;

import java.util.*;

@Mod.EventBusSubscriber(modid = CreateFreight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TradingConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> TRADING_RECIPES;

    private static final List<TradingRecipe> PARSED_RECIPES = new ArrayList<>();

    //定义配方的配置格式
    static {
        BUILDER.push("trading");

        TRADING_RECIPES = BUILDER
                .comment(
                        "在这里定义交易站的配方",
                        "格式: \"商品;商品数量|价格|交易上限|生物群系1:权重1,生物群系2:权重2,...\"",
                        "示例: \"minecraft:diamond;1|minecraft:emerald;10|64|minecraft:plains:5,minecraft:forest:3\""
                )
                .defineList(
                        "tradingRecipes",
                        List.of(),   // 默认值
                        obj -> obj instanceof String
                );
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static void parseRecipes() {
        PARSED_RECIPES.clear(); //每次重载时清空旧配方
        List<? extends String> configValues = TRADING_RECIPES.get();
        int recipeIndex = 0;

        for (String aRecipe : configValues) {
            try {
                String[] split = aRecipe.split("\\|");
                if (split.length != 4) {
                    throw new IllegalArgumentException("配方必须有4个部分，用'|'分隔!");
                }

                // 解析商品
                String[] sellParts = split[0].split(";");
                ItemStack sellStack = new ItemStack(
                        Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(sellParts[0]))),
                        Integer.parseInt(sellParts[1])
                );

                // 解析货币物品和价格
                String[] costParts = split[1].split(";");
                ItemStack costStack = new ItemStack(
                        Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(costParts[0]))),
                        Integer.parseInt(costParts[1])
                );

                // 解析交易上限
                int limit = Integer.parseInt(split[2]);

                // 解析区域权重
                Map<String, Integer> regionWeights = new HashMap<>();
                String[] weightKVs = split[3].split(",");

                for (String aWeightKV : weightKVs) {
                    String[] KeyVal = aWeightKV.split(":");
                    regionWeights.put(KeyVal[0], Integer.parseInt(KeyVal[1]));
                }

                // 生成ID
                ResourceLocation id = new ResourceLocation(CreateFreight.MODID, "configured/trade_" + recipeIndex++);

                PARSED_RECIPES.add(new TradingRecipe(id, sellStack, costStack, limit, regionWeights));

            } catch (Exception e) {
                System.err.println("Ignoring a wrong recipe '" + aRecipe + "': " + e.getMessage());
            }
        }
    }

    @SubscribeEvent
    public void onSetup(FMLCommonSetupEvent event) {
        parseRecipes();
    }

    @SubscribeEvent
    public void onLoad(ModConfigEvent.Reloading event) {
        parseRecipes();
    }

    public static ForgeConfigSpec.Builder getBUILDER() {
        return BUILDER;
    }

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> getTradingRecipes() {
        return TRADING_RECIPES;
    }

    public static ForgeConfigSpec getSPEC() {
        return SPEC;
    }
}
