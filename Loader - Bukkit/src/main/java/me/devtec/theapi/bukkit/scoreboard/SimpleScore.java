package me.devtec.theapi.bukkit.scoreboard;

import me.devtec.shared.Ref;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleScore {
    public static final Map<UUID, ScoreboardAPI> scores = new ConcurrentHashMap<>();
    private String name = "";
    private final List<String> lines = new ArrayList<>();

    public SimpleScore addLine(String line) {
        lines.add(line);
        return this;
    }

    public SimpleScore addLines(Collection<String> lines) {
        this.lines.addAll(lines);
        return this;
    }

    public SimpleScore setTitle(String title) {
        name = title;
        return this;
    }

    public void send(Player... players) {
        for (Player a : players) {
            ScoreboardAPI sb = getOrCreate(a);
            sb.setTitle(name);
            if (!Ref.isNewerThan(7)) {
                Collections.reverse(lines);
                if (lines.size() > 15){
                    lines.subList(15,lines.size()).clear();
                }
            }
            if (sb.getLines().size() > lines.size())
                sb.removeUpperLines(lines.size() - 1);
            int i = 0;
            for (String line : lines)
                sb.setLine(++i, line);
        }
        lines.clear();
    }

    private ScoreboardAPI getOrCreate(Player player) {
        ScoreboardAPI a = SimpleScore.scores.get(player.getUniqueId());
        if (a == null)
            SimpleScore.scores.put(player.getUniqueId(), a = new ScoreboardAPI(player, Ref.isNewerThan(7) ? 0 : -1));
        return a;
    }
}
