package me.DevTec.TheAPI.Utils;

import java.util.ArrayList;
import java.util.List;

public class Animation {
	private long last=System.currentTimeMillis();
	private long tics;
	int c;
	private List<String> lines = new ArrayList<>();
	public Animation(List<String> text, long ticks) {
		lines=text;
		tics=ticks;
	}
	
	public long getTicks() {
		return tics;
	}
	
	public String get() {
		if(c>=lines.size())c=0;
		if(last-System.currentTimeMillis()+tics <= 0) {
			last=System.currentTimeMillis();
			return lines.get(c++);
		}
		return lines.get(c);
	}
	
	public List<String> getLines() {
		return lines;
	}
}
