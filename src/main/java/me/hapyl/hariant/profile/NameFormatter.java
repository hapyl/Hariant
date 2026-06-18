package me.hapyl.hariant.profile;

import me.hapyl.hariant.database.rank.FormatRules;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface NameFormatter {
    
    @NotNull Component getNameFormatted(@NotNull FormatRules formatRules);
    
    @NotNull Component getNameFormatted();
    
    @NotNull Component getNameFormattedSocial();
    
}