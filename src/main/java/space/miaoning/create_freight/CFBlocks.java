package space.miaoning.create_freight;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import space.miaoning.create_freight.content.automatictrader.AutomaticTraderBlock;
import space.miaoning.create_freight.content.serverstore.ServerStoreBlock;


public class CFBlocks {
    private static final CreateRegistrate REGISTRATE = CreateFreight.getRegistrate();

    public static final BlockEntry<AutomaticTraderBlock> AUTOMATIC_TRADER = REGISTRATE
            .block("automatic_trader", AutomaticTraderBlock::new)
            .initialProperties(SharedProperties::stone)
            .simpleItem()
            .register();

//    public static final BlockEntry<Block> CARGO_STATION = REGISTRATE.block("cargo_station", Block::new)
//            .initialProperties(SharedProperties::stone)
//            .simpleItem()
//            .register();

    public static final BlockEntry<ServerStoreBlock> SERVER_STORE = REGISTRATE
            .block("server_store", ServerStoreBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(properties -> properties.strength(-1.0F, 3600000.0F))
            .simpleItem()
            .register();

    public static void register() {
    }

}

