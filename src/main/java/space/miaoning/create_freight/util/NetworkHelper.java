package space.miaoning.create_freight.util;

import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.packagerLink.LogisticsNetwork;

import java.util.UUID;

public class NetworkHelper {
    public static final UUID SERVER_ID = new UUID(0, 0);

    public static void initServerNetwork(UUID freqId) {
        LogisticsNetwork network = Create.LOGISTICS.logisticsNetworks
                .computeIfAbsent(freqId, $ -> new LogisticsNetwork(freqId));
        if (network.owner == null) {
            network.owner = SERVER_ID;
            network.locked = true;
            Create.LOGISTICS.markDirty();
        }
    }

    public static boolean isServerNetwork(UUID freqId) {
        return Create.LOGISTICS.logisticsNetworks.get(freqId).owner.equals(SERVER_ID);
    }
}
