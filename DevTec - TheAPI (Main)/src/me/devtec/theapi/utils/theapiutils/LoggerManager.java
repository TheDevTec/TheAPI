package me.devtec.theapi.utils.theapiutils;

import java.util.logging.LogRecord;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.listener.events.ConsoleLogEvent;
import me.devtec.theapi.utils.reflections.Ref;

public class LoggerManager {
	public static class BukkitLogger implements java.util.logging.Filter {
		public boolean isLoggable(LogRecord record) {
			ConsoleLogEvent e = new ConsoleLogEvent(record.getMessage(), format(record.getLevel().getName()));
			TheAPI.callEvent(e);
			record.setMessage(e.getMessage());
			return !e.isCancelled() && (e.getMessage() != null);
		}
	}
	
	public static class ConsoleLogger extends AbstractFilter {
		private static Filter.Result validateMessage(Message message, Level l) {
			if (message == null)
				return Filter.Result.DENY;
			ConsoleLogEvent e = new ConsoleLogEvent(message.getFormattedMessage(),format(l.name()));
			TheAPI.callEvent(e);
			Ref.set(message, "formattedMessage", e.getMessage());
			return e.isCancelled()?Filter.Result.DENY:(e.getMessage()==null?Filter.Result.DENY:Filter.Result.NEUTRAL);
		}
		
		private static Filter.Result validateMessage(String message, Level l) {
			if (message == null)
				return Filter.Result.DENY;
			ConsoleLogEvent e = new ConsoleLogEvent(message,format(l.name()));
			TheAPI.callEvent(e);
			message=e.getMessage();
			return e.isCancelled()?Filter.Result.DENY:(message==null?Filter.Result.DENY:Filter.Result.NEUTRAL);
		}
		
		public Filter.Result filter(LogEvent event) {
			return validateMessage(event.getMessage(), event.getLevel());
		}
		
		public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
			return validateMessage(msg, level);
		}
		
		public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
			return validateMessage(msg, level);
		}
		
		public Filter.Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
			if(msg instanceof Message)
				return validateMessage((Message)msg, level);
			return validateMessage(""+msg, level);
		}
	}
	
	private static String format(String s) {
		switch(s) {
		case "WARNING":
		case "WARN":
			return "WARNING";
		case "FATAL":
		case "ERROR":
		case "SEVERE":
			return "ERROR";
		}
		return s;
	}
}