package space.miaoning.create_freight;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import space.miaoning.create_freight.structure.FreqProcessor;
import space.miaoning.create_freight.structure.TradingPostProcessor;

public class CFStructureProcessors {
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSORS =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, CreateFreight.MODID);

    public static final RegistryObject<StructureProcessorType<FreqProcessor>> FREQ_PROCESSOR =
            STRUCTURE_PROCESSORS.register("freq_processor", () -> () -> FreqProcessor.CODEC);
    public static final RegistryObject<StructureProcessorType<TradingPostProcessor>> TRADING_POST_PROCESSOR =
            STRUCTURE_PROCESSORS.register("trading_post_processor", () -> () -> TradingPostProcessor.CODEC);
}
