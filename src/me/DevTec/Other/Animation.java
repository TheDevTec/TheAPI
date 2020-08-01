package me.DevTec.Other;

import java.util.List;

import com.google.common.collect.Lists;

public class Animation {
	private long last=System.currentTimeMillis();
	private int tics, c;
	private List<String> lines = Lists.newArrayList();
	public Animation(List<String> text, int ticks) {
		lines=text;
		tics=ticks;
	}
	
	public int getTicks() {
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
