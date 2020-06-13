package me.DevTec.NMS;

public class ConstructorPacket {
	private Packet a;
	public ConstructorPacket(Packet packet) {
		a=packet;
	}
	public boolean cancelled() {
		return a == null;
	}
	
	public Packet getPacket() {
		return a;
	}
	
	public void setPacket(Packet packet) {
		a=packet;
	}
}
