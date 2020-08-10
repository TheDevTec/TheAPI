package me.DevTec.Config;

public class Key {
	private final String name;
	private String comment;

	public Key(String name) {
		this.name=name;
	}
	
	public Key(String name, String comment) {
		this.name=name;
		this.comment=comment;
	}
	
	public String getComment() {
		return comment;
	}
	
	public String getName() {
		return name;
	}
}
