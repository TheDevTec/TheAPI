package me.DevTec.Config;

import java.util.Arrays;
import java.util.List;

public class Key {
	private final String name;
	private String[] comment;

	public Key(String name) {
		this.name=name;
	}
	
	public Key(String name, String... comment) {
		this.name=name;
		this.comment=comment;
	}
	
	public List<String> getComment() {
		return comment!=null?Arrays.asList(comment):Arrays.asList();
	}
	
	public String getName() {
		return name;
	}
}
