package me.DevTec.Config;

public class Action {
	private Object o;
	private int ac;
	private Section sec;
	
	public Action(Section s, int action, Object a) {
		sec=s;
		ac=action;
		o=a;
	}
	
	public int action() {
		return ac;
	}
	
	public Object object() {
		return o;
	}
	
	public Section section() {
		return sec;
	}
}
