package space.miaoning.create_freight;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

public class CFItems {

    private static final CreateRegistrate REGISTRATE = CreateFreight.getRegistrate();

    public static final ItemEntry<Item> GREEN_COIN = REGISTRATE
            .item("green_coin", Item::new)
            .properties(p -> p.stacksTo(1024))
            .lang("Green Coin")
            .register(); // 完成注册

    public static void register() {
    }
}
