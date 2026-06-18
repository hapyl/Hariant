package me.hapyl.hariant.profile;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.annotate.PartiallyConstructed;
import me.hapyl.hariant.database.rank.FormatRules;
import me.hapyl.hariant.database.rank.RankFormatter;
import me.hapyl.hariant.entity.Lifecycle;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Map;
import java.util.function.Predicate;

public class VanillaTeamManager implements Ticking, Lifecycle {
    
    private final Scoreboard scoreboard;
    private final PlayerProfile profile;
    
    private final Map<PlayerProfile, State> profileStateMap;
    
    public VanillaTeamManager(@NotNull Scoreboard scoreboard, @NotNull @PartiallyConstructed PlayerProfile profile) {
        this.scoreboard = scoreboard;
        this.profile = profile;
        this.profileStateMap = Maps.newHashMap();
    }
    
    @Override
    public void tick() {
        Hariant.getPlayerProfiles().forEach(profile -> {
            final State state = profileStateMap.getOrDefault(profile, State.LOBBY);
            final Team team = this.getPlayerTeam(profile);
            
            state.tick(team, profile);
        });
    }
    
    @Override
    public void onCreate() {
        // On profile creation, we create teams for each profile (including self) with LOBBY state
        Hariant.getPlayerProfiles().forEach(profile -> this.setState(profile, State.LOBBY));
    }
    
    @Override
    public void onDestroy() {
    }
    
    public void setState(@NotNull PlayerProfile profile, @NotNull State state) {
        this.profileStateMap.put(profile, state);
        
        state.update(this.getPlayerTeam(profile), profile);
    }
    
    public void setStateForAllProfilesExceptSelf(@NotNull State state) {
        Hariant.getPlayerProfiles()
               .filter(Predicate.not(profile::equals))
               .forEach(profile -> this.setState(profile, state));
    }
    
    public void setStateForAllProfiles(boolean lobby) {
        Hariant.getPlayerProfiles()
               .forEach(profile -> this.setState(profile, lobby ? State.LOBBY : this.getRelation(profile)));
    }
    
    @NotNull
    private State getRelation(@NotNull PlayerProfile profile) {
        return profile.isSpectator()
               ? State.IN_GAME_SPECTATEE
               : this.profile.equals(profile) || this.profile.getTeam().isInTeam(profile)
                 ? State.IN_GAME_ALLY
                 : State.IN_GAME_ENEMY;
    }
    
    @NotNull
    private Team getPlayerTeam(@NotNull PlayerProfile profile) {
        final String teamName = "hariant_" + profile.getUuid();
        Team team = scoreboard.getTeam(teamName);
        
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.addEntry(profile.getPlayer().getScoreboardEntryName());
        }
        
        return team;
    }
    
    public static void bumpOtherProfilesToCreateTeamForMe(@NotNull PlayerProfile profile) {
        Hariant.getPlayerProfiles()
               .filter(Predicate.not(profile::equals))
               // We always create a lobby team for OTHER profile because joining while the game is in progress is not supported,
               // so it actually doesn't matter what state we create for, but using LOBBY is safest
               .forEach(otherProfile -> otherProfile.getPlayerUI().getVanillaTeamManager().setState(profile, State.LOBBY));
    }
    
    public static void bumpOtherProfilesToDeleteTeamForMe(@NotNull PlayerProfile profile) {
        Hariant.getPlayerProfiles()
               .filter(Predicate.not(profile::equals))
               .forEach(otherProfile -> {
                   final VanillaTeamManager teamManager = otherProfile.getPlayerUI().getVanillaTeamManager();
                   
                   teamManager.getPlayerTeam(profile).unregister();
                   teamManager.profileStateMap.remove(profile);
               });
    }
    
    public enum State {
        
        LOBBY {
            private static final FormatRules FORMAT_RULES = FormatRules.create(true, true, true, false, false);
            
            @Override
            public void update(@NotNull Team team, @NotNull PlayerProfile profile) {
                super.update(team, profile);
                
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                
                team.color(profile.getRank().formatter().getNameColor());
                team.setCanSeeFriendlyInvisibles(false);
            }
            
            @Override
            public void tick(@NotNull Team team, @NotNull PlayerProfile profile) {
                team.prefix(profile.getNameFormatted(FORMAT_RULES));
            }
        },
        
        IN_GAME_ALLY {
            @Override
            public void update(@NotNull Team team, @NotNull PlayerProfile profile) {
                super.update(team, profile);
                
                // It is actually fine to setting the name tag visibility to ALWAYS, since we're using
                // per-player scoreboard, so it will only affect the player whose scoreboard this is
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
                
                team.color(NamedTextColor.GREEN);
                team.setCanSeeFriendlyInvisibles(true);
            }
            
            @Override
            public void tick(@NotNull Team team, @NotNull PlayerProfile profile) {
                profile.getHariantPlayer().ifPresent(player -> team.suffix(player.createSuffix()));
            }
        },
        
        IN_GAME_ENEMY {
            @Override
            public void update(@NotNull Team team, @NotNull PlayerProfile profile) {
                super.update(team, profile);
                
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
                
                team.color(NamedTextColor.RED);
                team.setCanSeeFriendlyInvisibles(true);
            }
        },
        
        IN_GAME_SPECTATEE {
            @Override
            public void update(@NotNull Team team, @NotNull PlayerProfile profile) {
                super.update(team, profile);
                
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
                team.color(profile.getTeam().getColor());
            }
            
            @Override
            public void tick(@NotNull Team team, @NotNull PlayerProfile profile) {
                super.tick(team, profile);
                
                profile.getHariantPlayer().ifPresent(player -> team.suffix(player.createSuffix()));
            }
        };
        
        @OverridingMethodsMustInvokeSuper
        public void update(@NotNull Team team, @NotNull PlayerProfile profile) {
            team.prefix(null);
            team.suffix(null);
        }
        
        public void tick(@NotNull Team team, @NotNull PlayerProfile profile) {
        }
        
    }
    
}
