package dev.alphacentauri.randomvotekey;

import dev.alphacentauri.randomvotekey.commands.VoteKeyTimeLeftCommand;
import dev.alphacentauri.randomvotekey.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public final class RandomVoteKeyPlugin extends JavaPlugin {

    private static RandomVoteKeyPlugin INSTANCE;
    public static String NAME;
    public static String VERSION;
    public static String AUTHOR;
    public static long nextAnnounceTimestamp;

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        INSTANCE = this;

        NAME = getInstance().getDescription().getName();
        VERSION = "V" + getInstance().getDescription().getVersion();
        AUTHOR = getInstance().getDescription().getAuthors().get(0);

        saveDefaultConfig();
        registerCommand("votekeytimeleft", new VoteKeyTimeLeftCommand());

        String giveCommand;
        if (getConfig().getString("giveKeyCommand") == null) {
            Bukkit.getLogger().warning("GIVE COMMAND NOT SET!! The command to give a vote key has not been set, therefore the plugin is SHUTTING DOWN!");
            getInstance().getPluginLoader().disablePlugin(this);
            return;
        } else {
            giveCommand = getConfig().getString("giveKeyCommand");
        }

        boolean announcePublic = true;
        if (getConfig().get("announcePublic") != null) {
            announcePublic = getConfig().getBoolean("announcePublic");
        }

        String announcePublicMessage = CC.translate("&r\n&e&lFree Crate Key Event\n&b{player} &freceived vote key for being online!\n&fEvery 15 minutes, one random player gets &afree key&f.&r\n");
        if (getConfig().getString("announcePublicMessage") != null) {
            announcePublicMessage = CC.translate(getConfig().getString("announcePublicMessage"));
        }

        String keyReceivedMessage = CC.translate("&aYou received &evote key &afor being online!");
        if (getConfig().getString("keyReceivedMessage") != null) {
            keyReceivedMessage = CC.translate(getConfig().getString("keyReceivedMessage"));
        }

        String bossBarText = CC.translate("&a&lGIVING FREE KEY TO RANDOM PLAYER");
        if (getConfig().getString("bossBarText") != null) {
            bossBarText = CC.translate(getConfig().getString("bossBarText"));
        }

        int interval = 15;
        if (getConfig().getInt("timeInterval") != 0) {
            interval = getConfig().getInt("timeInterval");
        }

        String finalKeyReceivedMessage = keyReceivedMessage;
        boolean finalAnnouncePublic = announcePublic;

        nextAnnounceTimestamp = System.currentTimeMillis() + (interval * 60_000);

        final String finalAnnouncePublicMessage = announcePublicMessage;
        final int finalInterval = interval;
        final String finalBossBarText = bossBarText;

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (Bukkit.getOnlinePlayers().size() > 0) {
                ArrayList<Player> list = new ArrayList<>();

                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!online.hasPermission("randomvotekey.exclude")) {
                        list.add(online);
                    }
                }

                if (!list.isEmpty()) {
                    Player selected = list.get(new Random().nextInt(list.size()));

                    if (selected.getInventory().firstEmpty() != -1) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), giveCommand.replace("{player}", selected.getName()));
                        selected.sendMessage(finalKeyReceivedMessage);
                        selected.playSound(selected.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);

                        if (finalAnnouncePublic) {
                            BossBar bossBar = Bukkit.createBossBar(finalBossBarText, BarColor.GREEN, BarStyle.SOLID);
                            bossBar.setVisible(true);
                            bossBar.setProgress(0);

                            for (Player online : Bukkit.getOnlinePlayers()) {
                                if (online.getUniqueId() != selected.getUniqueId()) {
                                    online.sendMessage(finalAnnouncePublicMessage.replace("{player}", selected.getName()));
                                }

                                bossBar.addPlayer(online);
                            }

                            new BukkitRunnable() {
                                int count = 0;

                                @Override
                                public void run() {
                                    if (count == 8) {
                                        bossBar.removeAll();
                                        bossBar.setVisible(false);
                                        this.cancel();
                                    }

                                    count++;
                                }
                            }.runTaskTimer(this, 0, 20);

                            new BukkitRunnable() {
                                double count = 0;

                                @Override
                                public void run() {
                                    if (count <= 1) {
                                        bossBar.setProgress(count);
                                        count += .01;
                                    } else {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(this, 0, 1);
                        }

                        Bukkit.getConsoleSender().sendMessage("§6§lRANDOM VOTE KEY §7§l>> §fA vote key was given out to the player §a" + selected.getName());
                    }
                }
            }

            nextAnnounceTimestamp = System.currentTimeMillis() + (finalInterval * 60_000);
        }, interval * 20 * 60, interval * 20 * 60);

        getLogger().info(CC.GREEN + NAME + " has been SUCCESSFULLY loaded in " + (System.currentTimeMillis() - time) + "ms! This plugin is running version " + VERSION);
        getLogger().info(CC.GREEN + "This plugin was made by " + CC.YELLOW + AUTHOR);
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
        getLogger().info(CC.GREEN + NAME + " has SUCCESSFULLY been disabled.");
    }

    public static RandomVoteKeyPlugin getInstance() {
        return INSTANCE;
    }

    private void registerCommand(String command, Command commandClass){
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register(command, commandClass);
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
