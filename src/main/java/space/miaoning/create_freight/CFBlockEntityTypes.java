package space.miaoning.create_freight;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import space.miaoning.create_freight.content.automatictrader.AutomaticTraderBlockEntity;
import space.miaoning.create_freight.content.serverstore.ServerStoreBlockEntity;

public class CFBlockEntityTypes {
    private static final CreateRegistrate REGISTRATE = CreateFreight.getRegistrate();

    public static final BlockEntityEntry<AutomaticTraderBlockEntity> AUTOMATIC_TRADER = REGISTRATE
            .blockEntity("automatic_trader", AutomaticTraderBlockEntity::new)
            .validBlocks(CFBlocks.AUTOMATIC_TRADER)
            .register();

    public static final BlockEntityEntry<ServerStoreBlockEntity> SERVER_STORE = REGISTRATE
            .blockEntity("server_store", ServerStoreBlockEntity::new)
            .validBlocks(CFBlocks.SERVER_STORE)
            .register();

    public static void register() {
    }
}
