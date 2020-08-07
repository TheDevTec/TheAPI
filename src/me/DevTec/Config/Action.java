package me.DevTec.Config;

public class Action {
	private Object o;
	private String ac;
	private Section sec;
	
	public Action(Section s, String action, Object a) {
		sec=s;
		ac=action;
		o=a;
	}
	
	public String action() {
		return ac;
	}
	
	public Object object() {
		return o;
	}
	
	public Section section() {
		return sec;
	}
}
