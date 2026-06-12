package mc.mkay.lives;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;

public class LivesPlugin extends JavaPlugin {

    private static LivesPlugin instance;
    private LivesManager manager;

    @Override
    public void onEnable() {
        instance = this;
        manager = new LivesManager(this);
        getServer().getPluginManager().registerEvents(new LivesListener(this), this);
        getCommand("lives").setExecutor(new LivesCommand(this));
        getCommand("setlives").setExecutor(new LivesCommand(this));
        getCommand("resetlives").setExecutor(new LivesCommand(this));

        // Action bar updater — runs every 10 ticks (0.5 seconds)
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ActionBarUtil.sendNearbyLives(player, manager);
                }
            }
        }.runTaskTimer(this, 0L, 10L);

        getLogger().info("[Lives] Plugin enabled.");
    }

    @Override
    public void onDisable() {
        if (manager != null) manager.saveAll();
        getLogger().info("[Lives] Plugin disabled.");
    }

    public static LivesPlugin getInstance() { return instance; }
    public LivesManager getManager() { return manager; }
}
