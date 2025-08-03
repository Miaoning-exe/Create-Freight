package space.miaoning.create_freight;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

public class CFItems {

    private static final CreateRegistrate REGISTRATE = CreateFreight.getRegistrate();

    public static final ItemEntry<Item> CASH = REGISTRATE
            .item("cash", Item::new)
            .properties(p -> p.stacksTo(64))
            .lang("Cash")
            .register(); // 完成注册

    public static void register() {
    }
}
