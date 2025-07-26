package space.miaoning.create_freight;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import space.miaoning.create_freight.api.event.ModEvents;
import space.miaoning.create_freight.recipe.CFRecipeSerializers;
import space.miaoning.create_freight.recipe.CFRecipeTypes;

@Mod(CreateFreight.MODID)
public class CreateFreight {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "create_freight";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    static {
        REGISTRATE
                .setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }


    @SuppressWarnings("removal")
    public CreateFreight() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(new ModEvents());
        REGISTRATE.registerEventListeners(modEventBus);

        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        //注册创造物品栏
        REGISTRATE.setCreativeTab(CFCreativeTabs.MAIN);
        CFBlocks.register();
        CFBlockEntityTypes.register();
        CFCreativeTabs.register(modEventBus);

        CFRecipeSerializers.SERIALIZERS.register(modEventBus);
        CFRecipeTypes.RECIPE_TYPES.register(modEventBus);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

        });
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }
}
