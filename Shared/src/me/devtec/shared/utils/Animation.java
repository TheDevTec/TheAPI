package me.devtec.shared.utils;

import java.util.List;

public class Animation {
	private List<String> lines;
	private long last = System.currentTimeMillis()/50;
	private long ticks;
	private int pos;

	public Animation(List<String> text, long ticks) {
		lines = text;
		this.ticks = ticks;
	}

	public long getTicks() {
		return ticks;
	}

	public String get() {
		if(lines.isEmpty())return null;
		return lines.get(pos);
	}

	public void next() {
		if (last - System.currentTimeMillis()/50 + ticks <= 0) {
			last = System.currentTimeMillis()/50;
			if (++pos >= lines.size())pos = 0;
		}
	}
	
	public List<String> getLines() {
		return lines;
	}
}
