package space.miaoning.create_freight.api.event;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import space.miaoning.create_freight.CreateFreight;
import space.miaoning.create_freight.config.TradingConfig;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = CreateFreight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<BlockPos, UUID> PENDING_CHANNELS = new ConcurrentHashMap<>();

    /**
     * 在结构生成时，找到交易站的位置并为其分配一个唯一的UUID频道。
     */
    @SubscribeEvent
    public void onStructureGenerated(StructureGeneratedEvent event) {
        // TODO
        LOGGER.info("SUCCESS! Custom event 'StructureGeneratedEvent' has fired!");
    }
}
