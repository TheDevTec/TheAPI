package me.devtec.shared.sockets.implementation;

import java.io.File;

import me.devtec.shared.dataholder.Config;

public class SocketAction {
	public enum SocketActionEnum {
		FILE, DATA
	}

	SocketActionEnum action;
	Config config;
	String fileName;
	File file;

	public SocketAction(SocketActionEnum action, Config data, String fileName, File file) {
		this.action = action;
		config = data;
		this.fileName = fileName;
		this.file = file;
	}
}
