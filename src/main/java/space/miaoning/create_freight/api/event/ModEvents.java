package space.miaoning.create_freight.api.event;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;


import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<BlockPos, UUID> PENDING_CHANNELS = new ConcurrentHashMap<>();

    /**
     * 在结构生成时，找到交易站的位置并为其分配一个唯一的UUID频道。
     */
    @SubscribeEvent
    public void onStructureGenerated(StructureGeneratedEvent event) {
        LOGGER.info("==========================================================");
        LOGGER.info("SUCCESS! Custom event 'StructureGeneratedEvent' has fired!");
        LOGGER.info("==========================================================");
//        Structure structure = event.getStructure();
//        String structureId = event.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure).toString();

//        if (!structureId.startsWith(CreateFreight.MODID)) return;

//        UUID ChannelId = UUID.randomUUID();
    }

    @SubscribeEvent
    public void testEvent(BlockEvent.BreakEvent event) {
        LOGGER.info("测试事件被触发");
    }
}
