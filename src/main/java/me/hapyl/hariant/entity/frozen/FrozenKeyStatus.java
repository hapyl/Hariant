package me.hapyl.hariant.entity.frozen;

import me.hapyl.hariant.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrozenKeyStatus {
    
    private static final Style STYLE_CURRENT = Style.style(Colors.GRAY, TextDecoration.UNDERLINED);
    private static final Style STYLE_NOT_CURRENT = Style.style(Colors.DARK_GRAY);
    private static final Style STYLE_CORRECT = Style.style(Colors.GREEN);
    private static final Style STYLE_INCORRECT = Style.style(Colors.RED);
    
    private final FrozenKey key;
    private Boolean status;
    
    FrozenKeyStatus(@NotNull FrozenKey key) {
        this.key = key;
        this.status = null;
    }
    
    @NotNull
    public FrozenKey getKey() {
        return key;
    }
    
    @Nullable
    public Boolean getStatus() {
        return status;
    }
    
    public void setStatus(@NotNull Boolean status) {
        this.status = status;
    }
    
    @NotNull
    public Component asComponent(boolean isCurrentKey) {
        return key.getName().style(
                isCurrentKey
                ? STYLE_CURRENT
                : status == null
                  ? STYLE_NOT_CURRENT
                  : status
                    ? STYLE_CORRECT
                    : STYLE_INCORRECT
        );
    }
    
}