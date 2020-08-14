package me.DevTec.Bans;

import me.DevTec.Other.LoaderClass;
import me.DevTec.Other.StringUtils;

public class Report {
	private String s, p, r, m, id;
	private long l;

	public Report(String id, String sender, String reported, String reason, String message, long time) {
		s = sender;
		this.id = id;
		p = reported;
		r = reason;
		m = message;
		l = time;
	}

	public String getSender() {
		return s;
	}

	public String getReported() {
		return p;
	}

	public String getReason() {
		return r;
	}

	public void setMessage(String newMessage) {
		LoaderClass.data.set("report." + s + "." + id + ".message", newMessage);
		LoaderClass.data.save();
		m = newMessage;
	}

	public void setReason(String newReason) {
		LoaderClass.data.set("report." + s + "." + id + ".reason", newReason);
		LoaderClass.data.save();
		r = newReason;
	}

	public String getMessage() {
		return m;
	}

	public long getTime() {
		return l;
	}

	public int getID() {
		return StringUtils.getInt(id);
	}

	public boolean isSolved() {
		return LoaderClass.data.getBoolean("report." + s + "." + id + ".solved");
	}

	public void remove() {
		LoaderClass.data.set("report." + s + "." + id, null);
		LoaderClass.data.save();
	}

	public void setSolved(boolean solved) {
		LoaderClass.data.set("report." + s + "." + id + ".solved", solved);
		LoaderClass.data.save();
	}
}
