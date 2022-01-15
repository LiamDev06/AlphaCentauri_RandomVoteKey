package dev.alphacentauri.randomvotekey.commands;

import dev.alphacentauri.randomvotekey.RandomVoteKeyPlugin;
import dev.alphacentauri.randomvotekey.utils.CC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class VoteKeyTimeLeftCommand extends Command {

    public VoteKeyTimeLeftCommand() {
        super("votekeytimeleft");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.isOp() && !player.hasPermission("randomvotekey.checktimeleft")) {
                player.sendMessage(CC.translate("&cYou are not allowed to perform this!"));
            }

            long amountLeftTimestamp = RandomVoteKeyPlugin.nextAnnounceTimestamp - System.currentTimeMillis();
            player.sendMessage(CC.translate("&aThe key will be handed out in &b" + timeAsString(amountLeftTimestamp) + "&a."));
            return true;
        }

        long amountLeftTimestamp = RandomVoteKeyPlugin.nextAnnounceTimestamp - System.currentTimeMillis();
        sender.sendMessage(CC.translate("&aThe key will be handed out in &b" + timeAsString(amountLeftTimestamp) + "&a."));
        return false;
    }

    private String timeAsString(long timePeriod){

        long millis = timePeriod;

        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 1) output += days + " days ";
        else if (days == 1) output += days + " day ";

        if (hours > 1) output += hours + " hours ";
        else if (hours == 1) output += hours + " hour ";

        if (minutes > 1) output += minutes + " minutes ";
        else if (minutes == 1) output += minutes + " minute ";

        if (seconds > 1) output += seconds + " seconds ";
        else if (seconds == 1) output += seconds + " second ";

        if (seconds < 1) output += " now ";
        return output.trim();
    }

}












