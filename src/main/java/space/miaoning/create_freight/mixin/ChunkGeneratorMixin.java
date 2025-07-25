package space.miaoning.create_freight.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.logging.LogUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import space.miaoning.create_freight.api.event.StructureGeneratedEvent;

@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Unique
    private static final Logger create_freight$LOGGER = LogUtils.getLogger();

    @Inject(
            method = "tryGenerateStructure",
            at = @At("RETURN")
    )
    private void onTryGenerateStructure(StructureSet.StructureSelectionEntry pStructureSelectionEntry,
                                        StructureManager pStructureManager,
                                        RegistryAccess pRegistryAccess,
                                        RandomState pRandom,
                                        StructureTemplateManager pStructureTemplateManager,
                                        long pSeed, ChunkAccess pChunk,
                                        ChunkPos pChunkPos,
                                        SectionPos pSectionPos,
                                        @NotNull CallbackInfoReturnable<Boolean> cir,
                                        @Local StructureStart structurestart) {
        create_freight$LOGGER.info("<<<<< MIXIN INJECTION SUCCESSFUL! >>>>>");
        if (!cir.getReturnValue()) {
            return;
        }
        StructureGeneratedEvent event = new StructureGeneratedEvent(structurestart, pChunk);
        MinecraftForge.EVENT_BUS.post(event);

    }
}
