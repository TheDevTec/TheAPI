package me.devtec.theapi.sqlapi;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.Validator;

public class SQLAPI {
	private Connection sql;
	private final String host;
    private final String database;
    private final String username;
    private final String password;
	private final int port;

	public SQLAPI(String host, String database, String username, String password, int port) {
		this.host = host;
		this.database = database;
		this.username = username;
		this.password = password;
		this.port = port;
	}

	public SQLAPI(String host, String database, String username, String password) {
		this(host, database, username, password, 3306);
	}
	
	public void connect() {
		openConnection();
	}

	public boolean isConnected() {
		try {
			return sql != null && !sql.isClosed();
		} catch (Exception e) {
			return false;
		}
	}

	public void close() {
		try {
			sql.close();
		} catch (Exception e) {
		}
		sql = null;
	}

	public void reconnect() {
		try {
			sql.close();
		} catch (Exception e) {
		}
		try {
			if(sql==null||sql.isClosed()) {
				synchronized (LoaderClass.plugin) {
					sql = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + at, username, password);
				}
			}
		} catch (Exception e) {
			synchronized (LoaderClass.plugin) {
				try {
					sql = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + at, username, password);
				} catch (Exception er) {
					
				}
			}
		}
	}

	public void createTable(String table, String values) {
		Validator.validate(!isConnected(), "SQL connection is closed");
		execute("CREATE TABLE IF NOT EXISTS "+table+" ("+values+")");
	}

	public void deleteTable(String table) {
		Validator.validate(!isConnected(), "SQL connection is closed");
		execute("DROP TABLE "+table);
	}

	public void remove(String table, String lookingfor, String identifier) {
		Validator.validate(!isConnected(), "SQL connection is closed");
		execute("DELETE FROM "+table+" WHERE "+lookingfor+"='"+identifier+"'");
	}

	public List<Object> getTop(String table, String orderBy, String column, String lookingfor, String identifier, int limit) {
		Validator.validate(!isConnected(), "SQL connection is closed");
		List<Object> s = new ArrayList<>();
		try {
			ResultSet f = query("SELECT * FROM "+table+" WHERE "+lookingfor+"='"+identifier+"' ORDER BY "+orderBy+" DESC LIMIT "+limit);
			if(f!=null)
			while(f.next()) {
				s.add(f.getObject(column));
			}
		}catch(Exception er) {}
		return s;
	}

	public List<Object> getTop(String table, String orderBy, String column, int limit) {
		Validator.validate(!isConnected(), "SQL connection is closed");
		List<Object> s = new ArrayList<>();
		try {
			ResultSet f = query("SELECT * FROM "+table+" ORDER BY "+orderBy+" DESC LIMIT "+limit);
			if(f!=null)
			while(f.next()) {
				s.add(f.getObject(column));
			}
		}catch(Exception er) {}
		return s;
	}

	public void clearTable(String table) {
		Validator.validate(!isConnected(), "SQL connection is closed");
		execute("DELETE FROM "+table);
	}

	public boolean update(String command) {
		Validator.validate(command == null, "Command is null");
		boolean result = false;
		try {
			sql.createStatement().executeUpdate(command);
			result = true;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
		}
		return result;
	}

	public boolean largeUpdate(String command) {
		Validator.validate(command == null, "Command is null");
		boolean result = false;
		try {
			sql.createStatement().executeLargeUpdate(command);
			result = true;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
		}
		return result;
	}

	public ResultSet query(String command) {
		Validator.validate(command == null, "Command is null");
		ResultSet rs = null;
		try {
			rs = sql.createStatement().executeQuery(command);
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
		}
		return rs;
	}

	public int getInt(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null && s.next())
				return s.getInt(lookingfor);
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}

	public void insert(String table, String... values) {
		StringBuilder items = new StringBuilder();
		for (String s : values)
			items.append(s != null ? ", '" + s + "'" : ", null");
		if(!items.toString().trim().isEmpty()) {
			items = new StringBuilder(items.substring(2));
			String command = "INSERT INTO " + table + " VALUES (" + items + ")";
			execute(command);
		}
	}
	
	public void set(String table, String path, String value, String identifier, String idValue) {
		String command = "UPDATE " + table + " SET " + path + "='" + value + "' WHERE " + identifier + "='" + idValue
				+ "'";
		try {
			update(command);
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
		}
	}

	public long getLong(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null && s.next())
				return s.getLong(lookingfor);
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}

	public Array getArray(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null && s.next())
				return s.getArray(lookingfor);
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean getBoolean(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null && s.next())
				return s.getBoolean(lookingfor);
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public byte getByte(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null && s.next())
				return s.getByte(lookingfor);
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}

	public Object getObject(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null && s.next())
				return s.getObject(lookingfor);
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public BigDecimal getBigDecimal(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null && s.next())
				return s.getBigDecimal(lookingfor);
			return new BigDecimal(0);
		} catch (Exception e) {
			return new BigDecimal(0);
		}
	}

	public double getDouble(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null && s.next())
				return s.getDouble(lookingfor);
			return 0.0;
		} catch (Exception e) {
			return 0.0;
		}
	}

	public String getString(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null && s.next())
				return s.getString(lookingfor);
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean exists(String table, String identifier, String idValue) {
		String command = "SELECT * FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet q = query(command);
			return q!=null && q.next();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean execute(String command) {
		Validator.validate(command == null, "Command is null");
		try {
			sql.createStatement().execute(command);
			return true;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return false;
		}
	}

	private String at = "?autoReconnect=true&failOverReadOnly=false&maxReconnects=10"; //default attributes

	public String getConnectAttributes() {
		return at;
	}
	
	public void setConnectAttributes(String attributes) {
		at = attributes.startsWith("?")?attributes:"?"+attributes;
	}

	private void openConnection() {
		try {
			Validator.validate(isConnected(), "SQL connection is already open");
		} catch (Exception e1) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e1.printStackTrace();
			return;
		}
		synchronized (LoaderClass.plugin) {
			try {
				sql = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + at, username, password);
			} catch (Exception e) {
				if (!LoaderClass.config.getBoolean("Options.HideErrors"))
					e.printStackTrace();
			}
		}
	}
}
