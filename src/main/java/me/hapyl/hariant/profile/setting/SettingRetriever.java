package me.hapyl.hariant.profile.setting;

import org.jetbrains.annotations.NotNull;

public interface SettingRetriever {
    
    @NotNull <I> I getSetting(@NotNull Setting<I> setting);
    
    <I> void setSetting(@NotNull Setting<I> setting, @NotNull I value);
}
