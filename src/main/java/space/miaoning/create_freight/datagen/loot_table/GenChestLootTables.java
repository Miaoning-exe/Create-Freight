package space.miaoning.create_freight.datagen.loot_table;

import com.github.talrey.createdeco.ItemRegistry;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.logistics.packagerLink.PackagerLinkBlock;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;
import space.miaoning.create_freight.CFItems;
import space.miaoning.create_freight.CreateFreight;

import java.util.function.BiConsumer;

public class GenChestLootTables implements LootTableSubProvider {
    public static final ResourceLocation DESERT_TRADING_POST_CHEST = ResourceLocation.fromNamespaceAndPath(CreateFreight.MODID, "chests/desert_trading_post_chest");
    public static final ResourceLocation PLAIN_TRADING_POST_CHEST = ResourceLocation.fromNamespaceAndPath(CreateFreight.MODID, "chests/plain_trading_post_chest");
    public static final ResourceLocation OCEAN_TRADING_POST_CHEST = ResourceLocation.fromNamespaceAndPath(CreateFreight.MODID, "chests/ocean_trading_post_chest");
    public static final ResourceLocation JUNGLE_TRADING_POST_CHEST = ResourceLocation.fromNamespaceAndPath(CreateFreight.MODID, "chests/jungle_trading_post_chest");
    public static final ResourceLocation TAIGA_TRADING_POST_CHEST = ResourceLocation.fromNamespaceAndPath(CreateFreight.MODID, "chests/taiga_trading_post_chest");
    public static final ResourceLocation SNOWY_PLAIN_TRADING_POST_CHEST = ResourceLocation.fromNamespaceAndPath(CreateFreight.MODID, "chests/snowy_plain_trading_post_chest");

    public static final LootPool.Builder CoinsPool = LootPool.lootPool()
            .name("coins&cash")
            .setRolls(UniformGenerator.between(1, 3))
            .setBonusRolls(ConstantValue.exactly(2))

            .add(LootItem.lootTableItem(CFItems.CASH)
                    .setWeight(20)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(10, 25)))
            )
            .add(LootItem.lootTableItem(ItemRegistry.COINS.get("Copper").get())
                    .setWeight(20)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(5, 25)))
            )
            .add(LootItem.lootTableItem(ItemRegistry.COINS.get("Iron").get())
                    .setWeight(10)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 4)))
            )
            .add(LootItem.lootTableItem(ItemRegistry.COINS.get("Gold").get())
                    .setWeight(3)
                    .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
            );

    // 沙漠交易站战利品池
    private final LootTable.Builder desertTradingPost = LootTable.lootTable()
            .withPool(LootPool.lootPool()
                    .name("random_items")
                    .setRolls(UniformGenerator.between(3, 6))
                    .setBonusRolls(ConstantValue.exactly(1))

                    .add(LootItem.lootTableItem(Items.BREAD)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.IRON_INGOT)
                            .setWeight(5)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                    )
                    .add(LootItem.lootTableItem(AllItems.ZINC_NUGGET.get())
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 20)))
                    )
                    .add(LootItem.lootTableItem(AllBlocks.SHAFT.asItem())
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5)))
                    )
                    .add(LootItem.lootTableItem(Items.DEAD_BUSH)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.BONE)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.SAND)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 9)))
                    )
            )

            .withPool(CoinsPool);

    // 平原交易站战利品池
    private final LootTable.Builder plainTradingPost = LootTable.lootTable()
            .withPool(LootPool.lootPool()
                    .name("random_items")
                    .setRolls(UniformGenerator.between(3, 6))
                    .setBonusRolls(ConstantValue.exactly(1))

                    .add(LootItem.lootTableItem(Items.BREAD)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.IRON_INGOT)
                            .setWeight(5)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                    )
                    .add(LootItem.lootTableItem(AllItems.COPPER_NUGGET.get())
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 20)))
                    )
                    .add(LootItem.lootTableItem(AllBlocks.SHAFT.asItem())
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5)))
                    )
                    .add(LootItem.lootTableItem(Items.ROTTEN_FLESH)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.OAK_SAPLING)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3)))
                    )
            )

            .withPool(CoinsPool);

    // 海洋交易站战利品池
    private final LootTable.Builder oceanTradingPost = LootTable.lootTable()
            .withPool(LootPool.lootPool()
                    .name("random_items")
                    .setRolls(UniformGenerator.between(3, 6))
                    .setBonusRolls(ConstantValue.exactly(1))

                    .add(LootItem.lootTableItem(Items.BREAD)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.IRON_INGOT)
                            .setWeight(5)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                    )
                    .add(LootItem.lootTableItem(AllItems.COPPER_NUGGET.get())
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 20)))
                    )
                    .add(LootItem.lootTableItem(Items.SALMON)
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5)))
                    )
                    .add(LootItem.lootTableItem(Items.COD)
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.SCUTE)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                    )
                    .add(LootItem.lootTableItem(Items.FISHING_ROD)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                    )
            )

            .withPool(CoinsPool);

    // 丛林交易站战利品池
    private final LootTable.Builder jungleTradingPost = LootTable.lootTable()
            .withPool(LootPool.lootPool()
                    .name("random_items")
                    .setRolls(UniformGenerator.between(3, 6))
                    .setBonusRolls(ConstantValue.exactly(1))

                    .add(LootItem.lootTableItem(Items.MELON)
                            .setWeight(20)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8)))
                    )
                    .add(LootItem.lootTableItem(Items.IRON_INGOT)
                            .setWeight(5)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                    )
                    .add(LootItem.lootTableItem(AllItems.COPPER_NUGGET.get())
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 20)))
                    )
                    .add(LootItem.lootTableItem(Items.PUMPKIN_SEEDS)
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5)))
                    )
                    .add(LootItem.lootTableItem(Items.COCOA_BEANS)
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.REDSTONE)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                    )
            )

            .withPool(CoinsPool);

    // 针叶林交易站战利品池
    private final LootTable.Builder taigaTradingPost = LootTable.lootTable()
            .withPool(LootPool.lootPool()
                    .name("random_items")
                    .setRolls(UniformGenerator.between(3, 6))
                    .setBonusRolls(ConstantValue.exactly(1))

                    .add(LootItem.lootTableItem(Items.SWEET_BERRIES)
                            .setWeight(20)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8)))
                    )
                    .add(LootItem.lootTableItem(Items.IRON_INGOT)
                            .setWeight(5)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 20)))
                    )
                    .add(LootItem.lootTableItem(Items.COAL)
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5)))
                    )
                    .add(LootItem.lootTableItem(Items.GLASS)
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.POWDER_SNOW_BUCKET)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                    )
            )

            .withPool(CoinsPool);

    // 雪原交易站战利品池
    private final LootTable.Builder snowyPlainTradingPost = LootTable.lootTable()
            .withPool(LootPool.lootPool()
                    .name("random_items")
                    .setRolls(UniformGenerator.between(3, 6))
                    .setBonusRolls(ConstantValue.exactly(1))

                    .add(LootItem.lootTableItem(Items.BREAD)
                            .setWeight(20)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8)))
                    )
                    .add(LootItem.lootTableItem(Items.IRON_INGOT)
                            .setWeight(5)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.GOLD_NUGGET)
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 20)))
                    )
                    .add(LootItem.lootTableItem(Items.COAL)
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5)))
                    )
                    .add(LootItem.lootTableItem(Items.BEETROOT)
                            .setWeight(15)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 3)))
                    )
                    .add(LootItem.lootTableItem(Items.HONEY_BOTTLE)
                            .setWeight(10)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                    )
            )

            .withPool(CoinsPool);

    @Override
    public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> pConsumer) {
        pConsumer.accept(DESERT_TRADING_POST_CHEST, desertTradingPost);
        pConsumer.accept(PLAIN_TRADING_POST_CHEST, plainTradingPost);
        pConsumer.accept(OCEAN_TRADING_POST_CHEST, oceanTradingPost);
        pConsumer.accept(JUNGLE_TRADING_POST_CHEST, jungleTradingPost);
        pConsumer.accept(TAIGA_TRADING_POST_CHEST, taigaTradingPost);
        pConsumer.accept(SNOWY_PLAIN_TRADING_POST_CHEST, snowyPlainTradingPost);
    }
}
