package me.hapyl.hariant.util;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.annotate.SelfReturn;
import me.hapyl.eterna.module.util.Buildable;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.task.HariantTickingTask;
import me.hapyl.hariant.task.Scheduler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ComponentShine {
    
    private final Map<? extends Integer, ? extends Component> frames;
    
    private ComponentShine(@NotNull Map<? extends Integer, ? extends Component> frames) {
        this.frames = frames;
    }
    
    public void display(@NotNull Audience audience, @NotNull Component subtitle, int period) {
        new HariantTickingTask(Scheduler.ofTimer(period)) {
            private final int duration = frames.size() - 1;
            private final int wait = Math.clamp(duration, 5, 20);
            
            @Override
            public void run(int tick) {
                if (tick > duration) {
                    this.cancel();
                    return;
                }
                
                audience.showTitle(Title.title(frames.get(tick), subtitle, 0, 5, wait));
            }
        };
    }
    
    public void display(@NotNull Audience audience, @NotNull Component subtitle) {
        this.display(audience, subtitle, 1);
    }
    
    public void display(@NotNull Audience audience) {
        this.display(audience, Component.empty(), 1);
    }
    
    public static @NotNull Builder builder(@NotNull String string) {
        return new Builder(string);
    }
    
    public static class Builder implements Buildable<ComponentShine> {
        
        private static final Style DEFAULT_STYLE = Style.style(Colors.WHITE);
        private static final Style DEFAULT_STYLE_SHINE = Style.style(Colors.YELLOW);
        private static final Style DEFAULT_STYLE_FADE = Style.style(Colors.ORANGE);
        
        private final char[] chars;
        
        private @NotNull Style style;
        private @NotNull Style styleShine;
        private @NotNull Style styleFade;
        
        Builder(@NotNull String string) {
            this.chars = string.toCharArray();
            this.style = DEFAULT_STYLE;
            this.styleShine = DEFAULT_STYLE_SHINE;
            this.styleFade = DEFAULT_STYLE_FADE;
        }
        
        @SelfReturn
        public Builder style(@NotNull Style style) {
            this.style = style;
            return this;
        }
        
        @SelfReturn
        public Builder styleShine(@NotNull Style styleShine) {
            this.styleShine = styleShine;
            return this;
        }
        
        @SelfReturn
        public Builder styleFade(@NotNull Style styleFade) {
            this.styleFade = styleFade;
            return this;
        }
        
        @Override
        public @NotNull ComponentShine build() {
            final Map<Integer, Component> frames = Maps.newHashMap();
            
            for (int i = 0; i < chars.length + 2; i++) {
                final TextComponent.Builder builder = Component.text();
                
                for (int j = 0; j < chars.length; j++) {
                    final Style charStyle = j == i ? styleShine : i > 0 && j == i - 1 ? styleFade : style;
                    
                    builder.append(Component.text(chars[j], charStyle));
                }
                
                frames.put(i, builder.build());
            }
            
            return new ComponentShine(frames);
        }
    }
    
}
