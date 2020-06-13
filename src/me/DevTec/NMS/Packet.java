package me.DevTec.NMS;

public class Packet {
	private Object a;
	public Packet(Object packet) {
		a=packet;
	}
	
	public Object getPacket() {
		return a;
	}
	
	public void setPacket(Object newPacket) {
		a=newPacket;
	}
}
