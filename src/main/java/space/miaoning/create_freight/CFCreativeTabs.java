package space.miaoning.create_freight;

import com.simibubi.create.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class CFCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateFreight.MODID);

    public static final List<ItemProviderEntry<?>> ITEMS = List.of(
            CFBlocks.AUTOMATIC_TRADER,
//            CFBlocks.CARGO_STATION,
            CFBlocks.SERVER_STORE,
            CFItems.GREEN_COIN
    );

    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_freight.main"))
            .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey())
            .icon(() -> CFBlocks.AUTOMATIC_TRADER.get().asItem().getDefaultInstance())
            .displayItems((displayParameters, output) -> ITEMS.forEach(output::accept))
            .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }

}
