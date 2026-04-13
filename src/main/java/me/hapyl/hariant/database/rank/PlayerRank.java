package me.hapyl.hariant.database.rank;

import me.hapyl.eterna.module.component.Components;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.database.PlayerDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum PlayerRank implements Rank {
    
    DEFAULT(new RankImpl(0, new RankFormatter() {
        private final Style styleMessage = Style.style(TextColor.color(0xBABABA));
        
        @Override
        @NotNull
        public Component getPrefix() {
            return Component.empty();
        }
        
        @Override
        @NotNull
        public NamedTextColor getNameColor() {
            return NamedTextColor.GRAY;
        }
        
        @NotNull
        @Override
        public Style getMessageStyle() {
            return styleMessage;
        }
    })),
    
    ADMIN(new RankImpl(101, new RankFormatter() {
        private final Style styleMessage = Style.style(TextColor.color(0xBA978F));
        
        @NotNull
        @Override
        public Component getPrefix() {
            return Component.text("ᴀᴅᴍɪɴ", TextColor.color(0xFF2F27), TextDecoration.BOLD);
        }
        
        @NotNull
        @Override
        public NamedTextColor getNameColor() {
            return NamedTextColor.RED;
        }
        
        @NotNull
        @Override
        public Style getMessageStyle() {
            return styleMessage;
        }
        
        @Override
        public boolean displayJoinMessages() {
            return false;
        }
    })),
    
    CONSOLE(
            new RankImpl(102, new RankFormatter() {
                @Override
                @NotNull
                public Component getPrefix() {
                    return Component.text("[Console]", TextColor.color(0xCA7AEB));
                }
                
                @NotNull
                @Override
                public NamedTextColor getNameColor() {
                    return NamedTextColor.DARK_RED;
                }
                
                @NotNull
                @Override
                public Style getMessageStyle() {
                    return Style.empty();
                }
            })
    ),
    
    ;
    
    public static final int PERMISSION_LEVEL_STAFF = 100;
    
    private final Rank rank;
    
    PlayerRank(@NotNull Rank rank) {
        this.rank = rank;
    }
    
    @Override
    public int permissionLevel() {
        return rank.permissionLevel();
    }
    
    @NotNull
    @Override
    public RankFormatter formatter() {
        return rank.formatter();
    }
    
    @NotNull
    public static PlayerRank getRank(@NotNull CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return CONSOLE;
        }
        else if (sender instanceof Player player) {
            return Hariant.getPlayerDatabase(player).getRank();
        }
        else {
            return DEFAULT;
        }
    }
}
