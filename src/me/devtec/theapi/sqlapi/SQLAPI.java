package me.devtec.theapi.sqlapi;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.Validator;

public class SQLAPI {
	private Connection connection;
	private String host, database, username, password;
	private int port;

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

	private boolean connected;

	public void connect() {
		openConnection();
		connected = true;
	}

	public boolean isConnected() {
		try {
			return connected && connection != null && !connection.isClosed();
		} catch (Exception e) {
			return false;
		}
	}

	public void close() {
		Validator.validate(!connected, "SQL connection is closed");
		Validator.validate(connection == null, "SQL connection is null");
		try {
			connection.close();
		} catch (Exception e) {
		}
		connection = null;
		connected = false;
	}

	public void reconnect() {
		close();
		connect();
	}

	public PreparedStatement getPreparedStatement(String command) {
		Validator.validate(!connected, "SQL connection is closed");
		Validator.validate(command == null, "Command is null");
		try {
			return connection.prepareStatement(command);
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return null;
		}
	}

	public boolean update(String command) {
		return update(getPreparedStatement(command));
	}

	public boolean update(PreparedStatement command) {
		Validator.validate(!connected, "SQL connection is closed");
		Validator.validate(command == null, "Command is null");
		boolean result = false;
		try {
			command.executeUpdate();
			result = true;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
		}
		return result;
	}

	public boolean largeUpdate(String command) {
		return largeUpdate(getPreparedStatement(command));
	}

	public boolean largeUpdate(PreparedStatement command) {
		Validator.validate(!connected, "SQL connection is closed");
		Validator.validate(command == null, "Command is null");
		boolean result = false;
		try {
			command.executeLargeUpdate();
			result = true;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
		}
		return result;
	}

	public ResultSet query(String command) {
		return query(getPreparedStatement(command));
	}

	public ResultSet query(PreparedStatement command) {
		Validator.validate(!connected, "SQL connection is closed");
		Validator.validate(command == null, "Command is null");
		ResultSet rs = null;
		try {
			rs = command.executeQuery();
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
		}
		return rs;
	}

	public int getInt(String table, String lookingfor, String identifier, String idValue) {
		Validator.validate(!connected, "SQL connection is closed");
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while (s.next())
					return s.getInt(lookingfor);
			return 0;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return 0;
		}
	}

	public void insert(String table, String... values) {
		Validator.validate(!connected, "SQL connection is closed");
		String items = "";
		for (String s : values)
			items += ", '" + s + "'";
		items = items.replaceFirst(", ", "");
		String command = "INSERT INTO " + table + " VALUES (" + items + ")";
		execute(command);
	}

	public void set(String table, String path, String value, String identifier, String idValue) {
		Validator.validate(!connected, "SQL connection is closed");
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
		Validator.validate(!connected, "SQL connection is closed");
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while (s.next())
					return s.getLong(lookingfor);
			return 0;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return 0;
		}
	}

	public Array getArray(String table, String lookingfor, String identifier, String idValue) {
		Validator.validate(!connected, "SQL connection is closed");
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while (s.next())
					return s.getArray(lookingfor);
			return null;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return null;
		}
	}

	public boolean getBoolean(String table, String lookingfor, String identifier, String idValue) {
		Validator.validate(!connected, "SQL connection is closed");
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while (s.next())
					return s.getBoolean(lookingfor);
			return false;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return false;
		}
	}

	public byte getByte(String table, String lookingfor, String identifier, String idValue) {
		Validator.validate(!connected, "SQL connection is closed");
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null && s.next())
				return s.getByte(lookingfor);
			return 0;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return 0;
		}
	}

	public Object getObject(String table, String lookingfor, String identifier, String idValue) {
		Validator.validate(!connected, "SQL connection is closed");
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while (s.next())
					return s.getObject(lookingfor);
			return null;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return null;
		}
	}

	public BigDecimal getBigDecimal(String table, String lookingfor, String identifier, String idValue) {
		Validator.validate(!connected, "SQL connection is closed");
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while (s.next())
					return s.getBigDecimal(lookingfor);
			return new BigDecimal(0);
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return new BigDecimal(0);
		}
	}

	public double getDouble(String table, String lookingfor, String identifier, String idValue) {
		Validator.validate(!connected, "SQL connection is closed");
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while (s.next())
					return s.getDouble(lookingfor);
			return 0.0;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return 0.0;
		}
	}

	public String getString(String table, String lookingfor, String identifier, String idValue) {
		Validator.validate(!connected, "SQL connection is closed");
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while (s.next())
					return s.getString(lookingfor);
			return null;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
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
		return execute(getPreparedStatement(command));
	}

	public boolean execute(PreparedStatement command) {
		Validator.validate(!connected, "SQL connection is closed");
		Validator.validate(command == null, "Command is null");
		try {
			command.execute();
			return true;
		} catch (Exception e) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e.printStackTrace();
			return false;
		}
	}

	private String at = "";

	public void setConnectAttributes(String attributes) {
		at = attributes;
	}

	private void openConnection() {
		try {
			Validator.validate(connection != null && !connection.isClosed(), "SQL connection is already open");
		} catch (Exception e1) {
			if (!LoaderClass.config.getBoolean("Options.HideErrors"))
				e1.printStackTrace();
		}
		synchronized (LoaderClass.plugin) {
			if (isConnected())
				return;
			if (Ref.getClass("com.mysql.jdbc.Driver") != null)
				try {
					connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + at,
							username, password);
				} catch (Exception e) {
					if (!LoaderClass.config.getBoolean("Options.HideErrors"))
						e.printStackTrace();
				}
		}
	}
}
