package me.devtec.theapi.utils;

import java.util.List;

public class Animation {
	private List<String> lines;
	private long last = System.currentTimeMillis()/50, tics;
	private int c;

	public Animation(List<String> text, long ticks) {
		lines = text;
		tics = ticks;
	}

	public long getTicks() {
		return tics;
	}

	public String get() {
		if(lines.isEmpty())return null;
		return lines.get(c);
	}

	public void next() {
		if (last - System.currentTimeMillis()/50 + tics <= 0) {
			last = System.currentTimeMillis()/50;
			++c;
			if (c >= lines.size())
				c = 0;
		}
	}
	
	public List<String> getLines() {
		return lines;
	}
}
