package space.miaoning.create_freight.util;

import com.simibubi.create.Create;

import java.util.UUID;

public class NetworkHelper {
    public static final UUID SERVER_ID = new UUID(0, 0);

    public static boolean isServerNetwork(UUID freqId) {
        return Create.LOGISTICS.logisticsNetworks.get(freqId).owner.equals(SERVER_ID);
    }
}
