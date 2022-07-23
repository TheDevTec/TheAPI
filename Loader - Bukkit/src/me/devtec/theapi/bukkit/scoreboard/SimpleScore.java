package me.devtec.theapi.bukkit.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import me.devtec.shared.Ref;

public class SimpleScore {
	public static final Map<UUID, ScoreboardAPI> scores = new ConcurrentHashMap<>();
	private String name = "";
	private final List<String> lines = new ArrayList<>();

	public SimpleScore addLine(String line)
	{
		this.lines.add(line);
		return this;
	}

	public SimpleScore addLines(Collection<String> lines)
	{
		this.lines.addAll(lines);
		return this;
	}

	public SimpleScore setTitle(String title)
	{
		this.name = title;
		return this;
	}

	public void send(Player... players)
	{
		for (Player a : players) {
			ScoreboardAPI sb = this.getOrCreate(a);
			sb.setTitle(this.name);
			if (!Ref.isNewerThan(7)) {
				Collections.reverse(this.lines);
				if (this.lines.size() > 15)
					for (int i = 15; i < this.lines.size(); ++i)
						this.lines.remove(i);
			}
			if (sb.getLines().size() > this.lines.size())
				sb.removeUpperLines(this.lines.size() - 1);
			int i = 0;
			for (String line : this.lines)
				sb.setLine(++i, line);
		}
		this.lines.clear();
	}

	private ScoreboardAPI getOrCreate(Player player)
	{
		ScoreboardAPI a = SimpleScore.scores.get(player.getUniqueId());
		if (a == null)
			SimpleScore.scores.put(player.getUniqueId(), a = new ScoreboardAPI(player, Ref.isNewerThan(7) ? 0 : -1));
		return a;
	}
}
