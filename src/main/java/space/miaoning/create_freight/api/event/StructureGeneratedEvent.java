package space.miaoning.create_freight.api.event;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.eventbus.api.Event;

/**
 * 当一个结构实例 (StructureStart) 成功生成并即将被写入区块数据时触发。
 * 这个事件不是可取消的，主要用于在结构生成后获取其数据。
 */
public class StructureGeneratedEvent extends Event {
    private final StructureStart structureStart;
    private final ChunkAccess chunk;

    public StructureGeneratedEvent(StructureStart structureStart, ChunkAccess chunk) {
        this.structureStart = structureStart;
        this.chunk = chunk;
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
}
