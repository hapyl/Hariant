package me.hapyl.hariant.security;

import me.hapyl.hariant.util.HexId;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class KickReasonImpl implements KickReason {
    
    private final UUID uuid;
    private final HexId hexId;
    private final String reason;
    private final String details;
    
    KickReasonImpl(@NotNull UUID uuid, @NotNull String reason, @Nullable String details) {
        this.uuid = uuid;
        this.hexId = HexId.ofRandom();
        this.reason = reason;
        this.details = details;
    }
    
    @NotNull
    @Override
    public HexId id() {
        return hexId;
    }
    
    @Override
    @NotNull
    public UUID uuid() {
        return uuid;
    }
    
    @NotNull
    @Override
    public String reason() {
        return reason;
    }
    
    @Nullable
    @Override
    public String details() {
        return details;
    }
    
    @NotNull
    @Override
    public Document asDocument() {
        return new Document()
                .append("id", this.hexId.toString())
                .append("uuid", this.uuid.toString())
                .append("type", this.type().toString())
                .append("reason", this.reason)
                .append("details", Objects.requireNonNullElse(this.details, ""));
    }
    
    @NotNull
    @Override
    public Component asComponent() {
        final TextComponent.Builder builder = Component.text();
        builder.append(Component.text("ʏᴏᴜ ʜᴀᴠᴇ ʙᴇᴇɴ ᴋɪᴄᴋᴇᴅ", NamedTextColor.DARK_RED, TextDecoration.BOLD));
        builder.appendNewline();
        builder.appendNewline();
        builder.appendNewline();
        
        // Append reason
        builder.append(Component.text("Reason:", NamedTextColor.GRAY));
        builder.appendNewline();
        builder.append(Component.text(reason));
        
        // Append details
        if (details != null) {
            builder.appendNewline();
            builder.appendNewline();
            
            builder.append(Component.text("Details:", NamedTextColor.GRAY));
            builder.appendNewline();
            builder.append(Component.text(details, NamedTextColor.DARK_GRAY));
        }
        
        // Append id
        builder.appendNewline();
        builder.appendNewline();
        builder.append(Component.text("Punishment Id: %s".formatted(hexId), NamedTextColor.DARK_GRAY));
        
        return builder.build();
    }
    
}
