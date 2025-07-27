package space.miaoning.create_freight.datagen.loot_table;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
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
import space.miaoning.create_freight.CreateFreight;

import java.util.function.BiConsumer;

public class GenChestLootTables implements LootTableSubProvider {
    public static final ResourceLocation DESERT_TRADING_POST_CHEST = ResourceLocation.fromNamespaceAndPath(CreateFreight.MODID, "chests/trading_post_chest");

    @Override
    public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> pConsumer) {
        pConsumer.accept(DESERT_TRADING_POST_CHEST, desertTradingPost);
    }

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
                    .add(LootItem.lootTableItem(AllBlocks.SHAFT.get())
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
            );

    //没有将createdeco添加为前置导致箱子里无法爆金币
//                    .withPool(LootPool.lootPool()
//                    .name("coins")
//                    .setRolls(UniformGenerator.between(3, 6))
//                    .setBonusRolls(ConstantValue.exactly(5))
//          );
}
