package space.miaoning.create_freight.api.event;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.eventbus.api.Event;

/**
 * 当一个结构实例 (StructureStart) 成功生成并即将被写入区块数据时触发。
 * 这个事件不是可取消的，主要用于在结构生成后获取其数据。
 */
public class StructureGeneratedEvent extends Event {
    private final StructureStart structureStart;
    private final ChunkAccess chunk;
    private final StructureManager structureManager;
    private final RegistryAccess registryAccess;
    private final StructureTemplateManager structureTemplateManager;
    private final ChunkPos chunkPos;
    private final SectionPos sectionPos;

    public StructureGeneratedEvent(StructureStart structureStart, ChunkAccess chunk,
                                   StructureManager structureManager, RegistryAccess registryAccess,
                                   StructureTemplateManager structureTemplateManager,
                                   ChunkPos chunkPos, SectionPos sectionPos) {
        this.structureStart = structureStart;
        this.chunk = chunk;
        this.structureManager = structureManager;
        this.registryAccess = registryAccess;
        this.structureTemplateManager = structureTemplateManager;
        this.chunkPos = chunkPos;
        this.sectionPos = sectionPos;
    }

    public StructureStart getStructureStart() {
        return structureStart;
    }

    public Structure getStructure() {
        return structureStart.getStructure();
    }

    public ChunkAccess getChunk() {
        return chunk;
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

    public RegistryAccess getRegistryAccess() {
        return registryAccess;
    }

    public StructureTemplateManager getStructureTemplateManager() {
        return structureTemplateManager;
    }

    public ChunkPos getChunkPos() {
        return chunkPos;
    }

    public SectionPos getSectionPos() {
        return sectionPos;
    }
}
