package me.hapyl.hariant.entity.vanilla;

import me.hapyl.hariant.attribute.instance.Attributes;
import me.hapyl.hariant.entity.HariantEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Husk;
import org.jetbrains.annotations.NotNull;

public final class VanillaEntityTypeHusk extends VanillaEntity<Husk> {
    
    VanillaEntityTypeHusk(@NotNull Husk husk) {
        super(
                husk,
                Component.text("Husk"),
                HariantEntity.createHeadComponent("269b9734d0e7bf060fedc6bf7fec64e1f7ad6fc80b0fd8441ad0c7508c850d73"),
                Attributes.base(500, 50, 50)
        );
    }
    
}
