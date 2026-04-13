package me.hapyl.hariant.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.hapyl.eterna.module.resource.JsonResourceLoader;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.hariant.game.battleground.EnumBattleground;
import me.hapyl.hariant.game.type.EnumGameType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class HariantConfigImpl extends JsonResourceLoader implements HariantConfig {
    
    public HariantConfigImpl(@NotNull Plugin plugin) {
        super(plugin, "config.json");
    }
    
    @NotNull
    @Override
    public String databaseConnectionLink() {
        return get("database_connection_link").getAsString();
    }
    
    @NotNull
    @Override
    public EnumBattleground getSelectedBattleground() {
        return getEnumValue("selected_battleground", EnumBattleground.class, EnumBattleground.ARENA);
    }
    
    @Override
    public void setSelectedBattleground(@NotNull EnumBattleground battleground) {
        this.set("selected_battleground", new JsonPrimitive(battleground.name().toLowerCase()));
    }
    
    @NotNull
    @Override
    public EnumGameType getSelectedGameType() {
        return getEnumValue("selected_game_type", EnumGameType.class, EnumGameType.DEATHMATCH);
    }
    
    @Override
    public void setSelectedGameType(@NotNull EnumGameType gameType) {
        this.set("selected_game_type", new JsonPrimitive(gameType.name().toLowerCase()));
    }
    
    @NotNull
    private <E extends Enum<E>> E getEnumValue(@NotNull String key, @NotNull Class<E> enumClass, @NotNull E defaultValue) {
        final JsonElement jsonElement = get(key);
        
        if (jsonElement.isJsonNull()) {
            return defaultValue;
        }
        
        final String string = jsonElement.getAsString();
        
        return Enums.byName(enumClass, string, defaultValue);
    }
}
