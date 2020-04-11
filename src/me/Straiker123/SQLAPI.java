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
	    private boolean connected;
	    private Statement statement;
		public void connect() {
	        try {
	            openConnection();
	             statement = connection.createStatement();
	             connected=true;
	        } catch (ClassNotFoundException | SQLException e) {
	        	if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("opening SQL connection", "Uknown");
	        	}else {
	        	TheAPI.msg("&cA corrupt error when connecting to the SQL:",TheAPI.getConsole());
	        	e.printStackTrace();
	        	TheAPI.msg("&cEnd of error",TheAPI.getConsole());
	        }
	        }
	    }
	 
		public boolean isConnected() {
			boolean i = false;
			if(statement!=null && connected) {
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
	        	TheAPI.msg("&cA corrupt error when closing SQL connection:",TheAPI.getConsole());
	        	e.printStackTrace();
	        	TheAPI.msg("&cEnd of error",TheAPI.getConsole());
	        }}
			connection=null;
			connected=false;
		    }
		}
		
	    public void reconnect() {
	        close();
	        connect();
	    }

	    public boolean update(String command) {
	    	if(!connected) {
	    		Error.err("processing SQL update, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	    	}
	        if (command == null) {
	            return false;
	        }
	        boolean result = false;
	        try {
	        	statement.executeUpdate(command);
	            result = true;
	        }catch (Exception e) {
	        	if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("processing SQL update, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	        	}else {
	        	TheAPI.msg("&cA corrupt error when processing SQL update, command: "+command+", Result: "+(isConnected() ? "Uknown command:" : "SQL isn't connected:"),TheAPI.getConsole());
	        	e.printStackTrace();
	        	TheAPI.msg("&cEnd of error",TheAPI.getConsole());
	        }}
	        return result;
	    }

	    public boolean largeUpdate(String command) {
	    	if(!connected) {
	    		Error.err("processing SQL largeUpdate, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	    	}
	        if (command == null) {
	            return false;
	        }
	        boolean result = false;
	        try {
	        	statement.executeLargeUpdate(command);
	            result = true;
	        }catch (Exception e) {
	        	if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("processing SQL largeUpdate, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	        	}else {
	        	TheAPI.msg("&cA corrupt error when processing SQL largeUpdate, command: "+command+", Result: "+(isConnected() ? "Uknown command:" : "SQL isn't connected:"),TheAPI.getConsole());
	        	e.printStackTrace();
	        	TheAPI.msg("&cEnd of error",TheAPI.getConsole());
	        }}
	        return result;
	    }

	    public ResultSet query(String command) {
	    	if(!connected) {
	    		Error.err("processing SQL query, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	    	}
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
	        	TheAPI.msg("&cA corrupt error when processing SQL query, command: "+command+", Result: "+(isConnected() ? "Uknown command:" : "SQL isn't connected:"),TheAPI.getConsole());
	        	e.printStackTrace();
	        	TheAPI.msg("&cEnd of error",TheAPI.getConsole());
	        }}
	        return rs;
	    }
	    
	    public int getInt(String table, String lookingfor, String identifier, String idValue) {
	    	String command = "SELECT "+lookingfor+" FROM "+table+" WHERE "+identifier+"='"+idValue+"'";
	    	try {
	    		ResultSet s = query(command);
	    		if(s.next())
				return s.getInt(1);
	    		return 0;
			} catch (Exception e) {
				if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("processing SQL query, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	        	}else {
	        	TheAPI.msg("&cA corrupt error when processing SQL query, command: "+command+", Result: "+(isConnected() ? "Uknown command:" : "SQL isn't connected:"),TheAPI.getConsole());
	        	e.printStackTrace();
	        	TheAPI.msg("&cEnd of error",TheAPI.getConsole());
	        	}
	        	return 0;
			}
	    }

	    public double getDouble(String table, String lookingfor, String identifier, String idValue) {
	    	String command = "SELECT "+lookingfor+" FROM "+table+" WHERE "+identifier+"='"+idValue+"'";
	    	try {
	    		ResultSet s = query(command);
	    		if(s.next())
				return s.getDouble(1);
	    		return 0.0;
			} catch (Exception e) {
				if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("processing SQL query, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	        	}else {
	        	TheAPI.msg("&cA corrupt error when processing SQL query, command: "+command+", Result: "+(isConnected() ? "Uknown command:" : "SQL isn't connected:"),TheAPI.getConsole());
	        	e.printStackTrace();
	        	TheAPI.msg("&cEnd of error",TheAPI.getConsole());
	        	}
	        	return 0.0;
			}
	    }
	    
	    public boolean exists(String table, String lookingfor, String identifier, String idValue) {
	    	String command = "SELECT "+lookingfor+" FROM "+table+" WHERE "+identifier+"='"+idValue+"'";
	    	try {
				return query(command)!=null;
			} catch (Exception e) {
				if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("processing SQL query, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	        	}else {
	        	TheAPI.msg("&cA corrupt error when processing SQL query, command: "+command+", Result: "+(isConnected() ? "Uknown command:" : "SQL isn't connected:"),TheAPI.getConsole());
	        	e.printStackTrace();
	        	TheAPI.msg("&cEnd of error",TheAPI.getConsole());
	        	}
	        	return false;
			}
	    }
	    
	    public String getString(String table, String lookingfor, String identifier, String idValue) {
	    	String command = "SELECT "+lookingfor+" FROM "+table+" WHERE "+identifier+"='"+idValue+"'";
	    	try {
	    		ResultSet s = query(command);
	    		if(s.next())
				return s.getString(1);
	    		return null;
			} catch (Exception e) {
				if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("processing SQL query, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	        	}else {
	        	TheAPI.msg("&cA corrupt error when processing SQL query, command: "+command+", Result: "+(isConnected() ? "Uknown command:" : "SQL isn't connected:"),TheAPI.getConsole());
	        	e.printStackTrace();
	        	TheAPI.msg("&cEnd of error",TheAPI.getConsole());
	        	}
	        	return null;
			}
	    }
	    
	    public boolean execute(String command) {
	        if (command == null) {
	            return false;
	        }
	    	if(!connected) {
	    		Error.err("processing SQL execute, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	    		return false;
	    	}
	        try {
	           statement.execute(command);
	           return true;
	        }
	        catch (Exception e) {
	        	if(LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
	        		Error.err("processing SQL execute, command: "+command, (isConnected() ? "Uknown command" : "SQL isn't connected"));
	        	}else {
	        	TheAPI.msg("&cA corrupt error when processing SQL query, command: "+command+", Result: "+(isConnected() ? "Uknown command:" : "SQL isn't connected:"),TheAPI.getConsole());
	        	e.printStackTrace();
	        	TheAPI.msg("&cEnd of error",TheAPI.getConsole());
	        }
	        	return false;
	        }
	    }
	    
	    private String at="";
	    public void setConnectAttributes(String attributes) {
	    	at=attributes;
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
	        connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database+at, this.username, this.password);
	    }
}
}
