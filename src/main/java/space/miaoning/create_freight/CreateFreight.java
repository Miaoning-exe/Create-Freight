package space.miaoning.create_freight;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import space.miaoning.create_freight.api.event.ModEvents;
import space.miaoning.create_freight.config.TradingConfig;
import space.miaoning.create_freight.recipe.CFRecipeSerializers;
import space.miaoning.create_freight.recipe.CFRecipeTypes;

@Mod(CreateFreight.MODID)
public class CreateFreight {
    public static final String MODID = "create_freight";
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

        // 使用 CreateRegistrate 处理注册
        REGISTRATE.registerEventListeners(modEventBus);
        // 注册事件总线
        MinecraftForge.EVENT_BUS.register(new ModEvents());
        // 注册配置文件
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TradingConfig.SPEC, "create_freight-server.toml");
        // 注册其他
        CFBlocks.register();
        CFBlockEntityTypes.register();
        CFCreativeTabs.register(modEventBus);

        CFRecipeSerializers.SERIALIZERS.register(modEventBus);
        CFRecipeTypes.RECIPE_TYPES.register(modEventBus);

    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }
}
