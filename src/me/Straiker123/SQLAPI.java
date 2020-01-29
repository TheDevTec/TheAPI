package me.Straiker123;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.Straiker123.Utils.Error;

public class SQLAPI {
	   private Connection connection;
	    private String host, database, username, password;
	    private int port;
	 
	    public SQLAPI(String host, String database, String username, String password, int port) {
			this.host=host;
			this.database=database;
			this.username=username;
			this.password=password;
			this.port=port;
		}
	    private Statement statement;
		public void connect() {
	        try {    
	            openConnection();
	             statement = connection.createStatement();
	        } catch (ClassNotFoundException e) {
	        	if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("closing SQL connection", "Uknown");
	        	}else {
	        	TheAPI.getConsole().sendMessage(TheAPI.colorize("&cA corrupt error when connecting to the :"));
	        	e.printStackTrace();
	        	TheAPI.getConsole().sendMessage(TheAPI.colorize("&cEnd of error"));
	        }
	        } catch (SQLException e) {
	        	if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("closing SQL connection", "Uknown");
	        	}else {
	        	TheAPI.getConsole().sendMessage(TheAPI.colorize("&cA corrupt error when connecting to the SQL:"));
	        	e.printStackTrace();
	        	TheAPI.getConsole().sendMessage(TheAPI.colorize("&cEnd of error"));
	        }}
	    }
	 
		public boolean isConnected() {
			boolean i = false;
			if(statement!=null) {
			try {
				i=!statement.isClosed();
			} catch (SQLException e) {
			}
			}
			return i;
		}
		
		public void close() {
		    if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("closing SQL connection", "Uknown");
	        	}else {
	        	TheAPI.getConsole().sendMessage(TheAPI.colorize("&cA corrupt error when closing SQL connection:"));
	        	e.printStackTrace();
	        	TheAPI.getConsole().sendMessage(TheAPI.colorize("&cEnd of error"));
	        }}
			connection=null;
		    }
		}
	    public void reconnect() {
	        close();
	        connect();
	    }
	    
	    public boolean update(String command) {
	        if (command == null) {
	            return false;
	        }
	        boolean result = false;
	        try {
	        	statement.executeUpdate(command);
	        	statement.close();
	            result = true;
	        }
	        catch (Exception e) {
	        	if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("processing SQL update, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	        	
	        	}else {
	        	TheAPI.getConsole().sendMessage(TheAPI.colorize("&cA corrupt error when processing SQL update, command: "+command+", Result: "+(isConnected() ? "Uknown command:" : "SQL isn't connected:")));
	        	e.printStackTrace();
	        	TheAPI.getConsole().sendMessage(TheAPI.colorize("&cEnd of error"));
	        }}
	        return result;
	    }

	    public ResultSet query(String command) {
	        if (command == null) {
	            return null;
	        }
	        ResultSet rs = null;
	        try {
	            rs = statement.executeQuery(command);
	        }
	        catch (Exception e) {
	        	if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("processing SQL query, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	        	}else {
	        	TheAPI.getConsole().sendMessage(TheAPI.colorize("&cA corrupt error when processing SQL query, command: "+command+", Result: "+(isConnected() ? "Uknown command:" : "SQL isn't connected:")));
	        	e.printStackTrace();
	        	TheAPI.getConsole().sendMessage(TheAPI.colorize("&cEnd of error"));
	        }}
	        return rs;
	    }
		
	    private void openConnection() throws SQLException, ClassNotFoundException {
	    if (connection != null && !connection.isClosed()) {
	        return;
	    }
	 
	    synchronized (LoaderClass.plugin) {
	        if (connection != null && !connection.isClosed()) {
	            return;
	        }
	        Class.forName("com.mysql.jdbc.Driver");
	        connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
	    }
}
}
