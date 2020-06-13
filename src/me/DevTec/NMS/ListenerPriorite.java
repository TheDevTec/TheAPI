package me.DevTec.NMS;

public enum ListenerPriorite {
	LOWEST(0),
	LOW(1),
	NORMAL(2),
	HIGH(3),
	HIGHEST(4),
	MONITOR(5);
	private int a;
	ListenerPriorite(int id){
		a=id;
	}
	
	public int getId() {
		return a;
	}
}
