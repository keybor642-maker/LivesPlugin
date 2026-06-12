package mc.mkay.lives;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActionBarUtil {

    private static final double NEARBY_RADIUS = 30.0;

    public static void sendNearbyLives(Player viewer, LivesManager manager) {
        List<Player> nearby = new ArrayList<>();

        for (Player other : viewer.getWorld().getPlayers()) {
            if (other.equals(viewer)) continue;
            if (other.getLocation().distanceSquared(viewer.getLocation()) <= NEARBY_RADIUS * NEARBY_RADIUS) {
                nearby.add(other);
            }
        }

        // Sort by lives ascending (lowest first — most interesting)
        nearby.sort(Comparator.comparingInt(p -> manager.getLives(p)));

        // Build action bar — max 5 players shown to keep it clean
        int max = Math.min(nearby.size(), 5);
        if (max == 0) {
            // Show own lives when alone
            showOwnLives(viewer, manager);
            return;
        }

        Component bar = Component.empty();

        // Own lives first
        bar = bar.append(buildEntry(viewer, manager, true));

        for (int i = 0; i < max; i++) {
            bar = bar.append(Component.text("  ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
            bar = bar.append(buildEntry(nearby.get(i), manager, false));
        }

        viewer.sendActionBar(bar);
    }

    private static void showOwnLives(Player player, LivesManager manager) {
        player.sendActionBar(buildEntry(player, manager, true));
    }

    private static Component buildEntry(Player player, LivesManager manager, boolean isSelf) {
        int lives = manager.getLives(player);
        String name = isSelf ? "You" : player.getName();

        // Color based on lives
        NamedTextColor livesColor;
        if (lives >= 3) livesColor = NamedTextColor.GREEN;
        else if (lives == 2) livesColor = NamedTextColor.YELLOW;
        else if (lives == 1) livesColor = NamedTextColor.RED;
        else livesColor = NamedTextColor.DARK_RED;

        NamedTextColor nameColor = isSelf ? NamedTextColor.WHITE : NamedTextColor.GRAY;

        return Component.text(name, nameColor)
            .decoration(TextDecoration.ITALIC, false)
            .append(Component.text(" " + lives, livesColor)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false));
    }
}
