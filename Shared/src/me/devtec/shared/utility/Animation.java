package me.devtec.shared.utility;

import java.util.List;

public class Animation {
	private List<String> lines;
	private long last = System.currentTimeMillis() / 50;
	private long ticks;
	private int pos;

	public Animation(List<String> text, long ticks) {
		this.lines = text;
		this.ticks = ticks;
	}

	public long getTicks() {
		return this.ticks;
	}

	public String get() {
		if (this.lines.isEmpty())
			return null;
		return this.lines.get(this.pos);
	}

	public void next() {
		if (this.last - System.currentTimeMillis() / 50 + this.ticks <= 0) {
			this.last = System.currentTimeMillis() / 50;
			if (++this.pos >= this.lines.size())
				this.pos = 0;
		}
	}

	public List<String> getLines() {
		return this.lines;
	}
}
