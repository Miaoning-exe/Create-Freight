package space.miaoning.create_freight;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;


public class CFBlocks {
    private static final CreateRegistrate REGISTRATE = CreateFreight.getRegistrate();

    public static final BlockEntry<Block> CARGO_STATION = REGISTRATE.block("cargo_station", Block::new)
            .initialProperties(SharedProperties::stone)
            .simpleItem()
            .lang("Cargo Station")
            .register();

    public static void register() {
    }

}

