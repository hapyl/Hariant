package me.hapyl.hariant.security;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface KickReason extends Punishment, ComponentLike {
    
    @NotNull
    @Override
    default PunishmentType type() {
        return PunishmentType.KICK;
    }
    
    @Override
    @NotNull
    UUID uuid();
    
    @NotNull
    String reason();
    
    @Nullable
    default String details() {
        return null;
    }
    
    @Override
    @NotNull
    Document asDocument();
    
    @NotNull
    @Override
    Component asComponent();
    
    @NotNull
    static KickReason create(@NotNull UUID uuid, @NotNull String reason, @Nullable String details) {
        return new KickReasonImpl(uuid, reason, details);
    }
    
    @NotNull
    static KickReason create(@NotNull UUID uuid, @NotNull String reason) {
        return create(uuid, reason, null);
    }
    
}
