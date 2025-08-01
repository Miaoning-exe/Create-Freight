package space.miaoning.create_freight.api.event;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.config.TradingConfig;
import space.miaoning.create_freight.mixin.RecipeManagerMixin;
import space.miaoning.create_freight.recipe.TradingRecipe;

import java.util.HashMap;
import java.util.Map;


@Mod.EventBusSubscriber(modid = CreateFreight.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RecipeEventHandler {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server is starting, preparing for initial dynamic recipe load.");
        RecipeManager recipeManager = event.getServer().getRecipeManager();
        addRecipesFromConfig(recipeManager);
    }

//    /**
//     * 当数据包（包括配方和标签）加载/重载完成时触发。
//     */
//    @SubscribeEvent
//    public static void onTagsUpdated(TagsUpdatedEvent event) {
//        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
//            return;
//        }
//
//        // 获取服务器实例
//        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
//        if (server == null) {
//            LOGGER.error("Could not get current server instance to reload recipes.");
//            return;
//        }
//
//        RecipeManager recipeManager = server.getRecipeManager();
//
//        LOGGER.info("<<<<< Starting to load dynamic recipes from config for Create: Freight... >>>>>");
//        addRecipesFromConfig(recipeManager);
//    }

    /**
     * Use RecipeManagerMixin to modify the RecipeManager's recipe Map.
     * @param recipeManager The RecipeManager instance of the current server.
     */
    private static void addRecipesFromConfig(RecipeManager recipeManager) {
        var recipeStrings = TradingConfig.TRADING_RECIPES.get();
        if (recipeStrings.isEmpty()) {
            LOGGER.info("No dynamic recipes found in config. Skipping.");
            return;
        }

        Map<ResourceLocation, TradingRecipe> newRecipes = new HashMap<>();

        for (int i = 0; i < recipeStrings.size(); i++) {
            String recipeString = recipeStrings.get(i);
            try {
                ResourceLocation id = new ResourceLocation(CreateFreight.MODID, "from_config/trading_" + i);
                TradingRecipe recipe = parseRecipeFromString(id, recipeString);
                newRecipes.put(id, recipe);
            } catch (Exception e) {
                LOGGER.error("Failed to parse dynamic recipe at index {}: '{}'. Reason: {}", i, recipeString, e.getMessage());
            }
        }

        if (newRecipes.isEmpty()) {
            return;
        }

        try {
            var accessor = (RecipeManagerMixin) recipeManager;

            // 获取旧的配方Map
            Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipesByType = new HashMap<>();
            accessor.getRecipes().forEach((type, map) -> newRecipesByType.put(type, new HashMap<>(map)));

            newRecipes.forEach((id, recipe) -> {
                newRecipesByType.computeIfAbsent(recipe.getType(), t -> new HashMap<>()).put(id, recipe);
            });

            // 获取旧的 byName Map
            Map<ResourceLocation, Recipe<?>> newByName = new HashMap<>(accessor.getByName());
            newByName.putAll(newRecipes);

            // 将可变的Map转换为不可变Map再进行设置
            Map<RecipeType<?>, ImmutableMap<ResourceLocation, Recipe<?>>> immutableRecipesByType =
                    newRecipesByType.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, e -> ImmutableMap.copyOf(e.getValue())));

            accessor.setRecipes(immutableRecipesByType);
            accessor.setByName(ImmutableMap.copyOf(newByName));

            LOGGER.info("Successfully loaded and injected {} dynamic recipes from config using Mixin.", newRecipes.size());

        } catch (Exception e) {
            LOGGER.error("Could not inject dynamic recipes into RecipeManager using Mixin. Error: ", e);
        }
    }

    private static TradingRecipe parseRecipeFromString(ResourceLocation id, String recipeString) {
        String[] parts = recipeString.split("\\|");
        if (parts.length != 4) throw new IllegalArgumentException("Recipe string must have 4 parts separated by '|'");

        ItemStack sellStack = getItemStackFromString(parts[0]);
        ItemStack costStack = getItemStackFromString(parts[1]);

        int limit = Integer.parseInt(parts[2]);

        Map<String, Integer> regionWeights = new HashMap<>();

        // 注意：这里的分割符';'只用于区域权重
        String[] regionParts = parts[3].split(";");
        for (String regionPart : regionParts) {
            String[] weightParts = regionPart.split(",");
            regionWeights.put(weightParts[0], Integer.parseInt(weightParts[1]));
        }
        if (regionWeights.isEmpty()) throw new IllegalArgumentException("Region weights cannot be empty.");

        return new TradingRecipe(id, sellStack, costStack, limit, regionWeights);
    }

    private static ItemStack getItemStackFromString(String itemString) {
        try {
            itemString = itemString.trim();

            // 解析原生格式: "count item_id{NBT}"
            String[] parts = itemString.split(" ", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid format. Expected 'count item_id{nbt}'. Got: " + itemString);
            }
            int count = Integer.parseInt(parts[0]);
            String itemDefinition = parts[1];

            String itemIdString;
            String nbtPart = null;

            int nbtSeparatorIndex = itemDefinition.indexOf('{');
            if (nbtSeparatorIndex != -1) {
                itemIdString = itemDefinition.substring(0, nbtSeparatorIndex);
                nbtPart = itemDefinition.substring(nbtSeparatorIndex);
            } else {
                itemIdString = itemDefinition;
            }

            // 构建 ItemStack
            ResourceLocation itemId = new ResourceLocation(itemIdString);
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item == null) {
                throw new IllegalArgumentException("Item not found: " + itemIdString);
            }

            ItemStack itemStack = new ItemStack(item, count);

            if (nbtPart != null && !nbtPart.isEmpty()) {
                CompoundTag tag = TagParser.parseTag(nbtPart);
                itemStack.setTag(tag);
            }
            return itemStack;

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create ItemStack from string: '" + itemString + "'. Error: " + e.getMessage(), e);
        }
    }
}