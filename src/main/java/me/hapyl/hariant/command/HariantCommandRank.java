package me.hapyl.hariant.command;

import me.hapyl.eterna.module.command.ArgumentList;
import me.hapyl.eterna.module.util.StringList;
import me.hapyl.eterna.module.util.TypeConverter;
import me.hapyl.eterna.module.util.cache.Cache;
import me.hapyl.hariant.Colors;
import me.hapyl.hariant.Hariant;
import me.hapyl.hariant.HariantLogger;
import me.hapyl.hariant.database.rank.PlayerRank;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HariantCommandRank extends HariantCommand {
    
    private static final Cache<RankConfirmation> RANK_CONFIRMATION_CACHE = Cache.ofList(10_000L);
    
    public HariantCommandRank(@NotNull String name) {
        super(name, PlayerRank.ADMIN);
    }
    
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        // rank (target) [rank]
        final TypeConverter arg0 = args.get(0);
        
        // Check for confirmation
        if (arg0.toString().equals("approve")) {
            final RankConfirmation confirmation = RANK_CONFIRMATION_CACHE.match(_confirmation -> _confirmation.matchRequester(sender));
            
            if (confirmation == null) {
                HariantLogger.error(sender, Component.text("Nothing to approve!"));
                return;
            }
            
            confirmation.approve();
            RANK_CONFIRMATION_CACHE.remove(confirmation);
            
            HariantLogger.success(
                    sender,
                    Component.empty()
                             .append(Component.text("Confirmed modifying %s's rank from ".formatted(confirmation.target.getName())))
                             .append(confirmation.currentRank.formatter().getPrefix())
                             .append(Component.text(" ➲ "))
                             .append(confirmation.rankToSet.formatter().getPrefix())
                             .append(Component.text("!"))
            );
            return;
        }
        
        final Player target = arg0.toPlayer();
        
        if (target == null) {
            HariantLogger.error(sender, Component.text("This player is not online!"));
            return;
        }
        
        final PlayerRank currentRank = PlayerRank.getRank(target);
        final PlayerRank rankToSet = args.get(1).toEnum(PlayerRank.class);
        
        // If rank is null, show target's current rank
        if (rankToSet == null) {
            HariantLogger.success(
                    sender,
                    Component.empty()
                             .append(Component.text("%s's rank is ".formatted(target.getName())))
                             .append(currentRank.formatter().getPrefix())
                             .append(Component.text("."))
            );
        }
        // Otherwise, if current rank is staff or new rank is staff, create confirmation
        else if (currentRank.isStaff() || rankToSet.isStaff()) {
            final RankConfirmation existingConfirmation = RANK_CONFIRMATION_CACHE.match(_confirmation -> _confirmation.matchRequester(sender));
            
            if (existingConfirmation != null) {
                HariantLogger.error(sender, Component.text("You already have pending approval, either approve it first or wait for it to expire!"));
                return;
            }
            
            RANK_CONFIRMATION_CACHE.add(new RankConfirmation(sender, target, currentRank, rankToSet));
            
            HariantLogger.info(sender, Component.text("Since the rank you're trying to change is staff, an additional confirmation is required!"));
            HariantLogger.info(
                    sender,
                    Component.empty()
                             .append(Component.text("Run "))
                             .append(Component.text("/rank approve", Colors.ORANGE))
                             .append(Component.text(" within "))
                             .append(Component.text("%ds".formatted(RANK_CONFIRMATION_CACHE.getExpirationTime() / 1000L), Colors.NUMBER))
                             .append(Component.text(" to approve the rank change."))
            );
        }
        // Otherwise change the rank
        else {
            RankConfirmation.approve(sender, target, currentRank, rankToSet);
        }
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull ArgumentList args, @NotNull PlayerRank playerRank) {
        if (args.length == 1) {
            return StringList.ofOnlinePlayers();
        }
        else if (args.length == 2) {
            return StringList.ofEnumConstantLowercaseNames(PlayerRank.class);
        }
        
        return List.of();
    }
    
    private static final class RankConfirmation {
        
        private final @NotNull CommandSender requester;
        private final @NotNull Player target;
        private final @NotNull PlayerRank currentRank;
        private final @NotNull PlayerRank rankToSet;
        
        private transient boolean approved;
        
        private RankConfirmation(@NotNull CommandSender requester, @NotNull Player target, @NotNull PlayerRank currentRank, @NotNull PlayerRank rankToSet) {
            this.requester = requester;
            this.target = target;
            this.currentRank = currentRank;
            this.rankToSet = rankToSet;
        }
        
        public boolean matchRequester(@NotNull CommandSender requester) {
            return this.requester.equals(requester);
        }
        
        private void approve() {
            if (approved) {
                throw new SecurityException("Already approved!");
            }
            
            approved = true;
            
            // Set target's rank
            Hariant.getPlayerDatabase(target).setRank(rankToSet);
            
            // Notify
            final Component rankPrefix = rankToSet.formatter().getPrefix();
            
            HariantLogger.success(requester, Component.empty().append(Component.text("Set %s's rank to ".formatted(target.getName()))).append(rankPrefix).append(Component.text("!")));
            
            HariantLogger.info(target, Component.empty().append(Component.text("Your rank has been set to ")).append(rankPrefix).append(Component.text("!")));
            HariantLogger.info(target, Component.text("You may need to re-log to see the changes!"));
        }
        
        public static void approve(@NotNull CommandSender requester, @NotNull Player target, @NotNull PlayerRank currentRank, @NotNull PlayerRank rankToSet) {
            if (rankToSet.isStaff()) {
                throw new IllegalArgumentException("Cannot approve staff rank this way, create a confirmation!");
            }
            
            new RankConfirmation(requester, target, currentRank, rankToSet).approve();
        }
        
    }
    
}