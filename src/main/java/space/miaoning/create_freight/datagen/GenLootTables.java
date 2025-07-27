package space.miaoning.create_freight.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import space.miaoning.create_freight.datagen.loot_table.GenChestLootTables;

import java.util.List;
import java.util.Set;


public class GenLootTables extends LootTableProvider {

    public GenLootTables(PackOutput pOutput) {
        super(pOutput, Set.of(), List.of(
                new SubProviderEntry(GenChestLootTables::new, LootContextParamSets.CHEST)   // 生成宝箱战利品
        ));
    }
}
