package space.miaoning.create_freight;

import com.google.common.eventbus.Subscribe;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import space.miaoning.create_freight.event.StructureGeneratedEvent;

@Mod(CreateFreight.MODID)
public class eventHandler {
    @SubscribeEvent
    public static void onStructureGenerated(StructureGeneratedEvent event) {
        String structureId = event.getStructure().toString();

    }
}
