package me.DevTec;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.DevTec.Other.LoaderClass;
import me.DevTec.Utils.Error;

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
		try {
			openConnection();
			connected = true;
		} catch (ClassNotFoundException | SQLException e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("opening SQL connection", "Uknown");
			} else {
				TheAPI.msg("&cA corrupt error when connecting to the SQL:", TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
		}
	}

	public boolean isConnected() {
		boolean i = false;
		if (connected) {
			try {
				i = !connection.isClosed();
			} catch (Exception e) {
			}
		}
		return i;
	}

	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
					Error.err("closing SQL connection", "Uknown");
				} else {
					TheAPI.msg("&cA corrupt error when closing SQL connection:", TheAPI.getConsole());
					e.printStackTrace();
					TheAPI.msg("&cEnd of error", TheAPI.getConsole());
				}
			}
			connection = null;
			connected = false;
		}
	}

	public void reconnect() {
		close();
		connect();
	}

	public boolean update(String command) {
		if (!connected) {
			Error.err("processing SQL update, command: " + command,
					(isConnected() ? "Uknown command" : "SQL isn't connected"));
		}
		if (command == null) {
			return false;
		}
		boolean result = false;
		try {
			connection.prepareStatement(command).executeUpdate(command);
			result = true;
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL update, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL update, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
		}
		return result;
	}

	public boolean largeUpdate(String command) {
		if (!connected) {
			Error.err("processing SQL largeUpdate, command: " + command,
					(isConnected() ? "Uknown command" : "SQL isn't connected"));
		}
		if (command == null) {
			return false;
		}
		boolean result = false;
		try {
			connection.prepareStatement(command).executeLargeUpdate(command);
			result = true;
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL largeUpdate, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL largeUpdate, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
		}
		return result;
	}

	public ResultSet query(String command) {
		if (!connected) {
			Error.err("processing SQL query, command: " + command,
					(isConnected() ? "Uknown command" : "SQL isn't connected"));
		}
		if (command == null) {
			return null;
		}
		ResultSet rs = null;
		try {
			rs = connection.prepareStatement(command).executeQuery(command);
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL query, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
		}
		return rs;
	}

	public int getInt(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while(s.next())
				return s.getInt(lookingfor);
			return 0;
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL query, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
			return 0;
		}
	}

	public void insert(String table, String... values) {
		String items = "";
		for (String s : values)
			items += ", '" + s + "'";
		items = items.replaceFirst(", ", "");
		String command = "INSERT INTO " + table + " VALUES (" + items + ")";
		execute(command);
	}

	public void set(String table, String path, String value, String identifier, String idValue) {
		String command = "UPDATE " + table + " SET " + path + "='" + value + "' WHERE " + identifier + "='" + idValue
				+ "'";
		try {
			update(command);
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL update, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL update, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
		}
	}

	public long getLong(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while(s.next())
				return s.getLong(lookingfor);
			return 0;
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL query, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
			return 0;
		}
	}

	public Array getArray(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while(s.next())
				return s.getArray(lookingfor);
			return null;
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL query, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
			return null;
		}
	}

	public boolean getBoolean(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while(s.next())
				return s.getBoolean(lookingfor);
			return false;
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL query, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
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
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL query, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
			return 0;
		}
	}

	public Object getObject(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while(s.next())
				return s.getObject(lookingfor);
			return null;
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL query, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
			return null;
		}
	}

	public BigDecimal getBigDecimal(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while(s.next())
				return s.getBigDecimal(lookingfor);
			return new BigDecimal(0);
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL query, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
			return new BigDecimal(0);
		}
	}

	public double getDouble(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while(s.next())
				return s.getDouble(lookingfor);
			return 0.0;
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL query, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
			return 0.0;
		}
	}

	public String getString(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s != null)
				while(s.next())
				return s.getString(lookingfor);
			return null;
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL query, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
			return null;
		}
	}

	public boolean exists(String table, String lookingfor, String identifier, String idValue) {
		String command = "SELECT " + lookingfor + " FROM " + table + " WHERE " + identifier + "='" + idValue + "'";
		try {
			ResultSet s = query(command);
			if (s == null)
				return false;
			return s.next();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean execute(String command) {
		if (command == null) {
			return false;
		}
		if (!connected) {
			Error.err("processing SQL execute, command: " + command,
					(isConnected() ? "Uknown command" : "SQL isn't connected"));
			return false;
		}
		try {
			connection.prepareStatement(command).execute(command);
			return true;
		} catch (Exception e) {
			if (LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				Error.err("processing SQL execute, command: " + command,
						(isConnected() ? "Uknown command" : "SQL isn't connected"));
			} else {
				TheAPI.msg("&cA corrupt error when processing SQL query, command: " + command + ", Result: "
						+ (isConnected() ? "Uknown command:" : "SQL isn't connected:"), TheAPI.getConsole());
				e.printStackTrace();
				TheAPI.msg("&cEnd of error", TheAPI.getConsole());
			}
			return false;
		}
	}

	private String at = "";

	public void setConnectAttributes(String attributes) {
		at = attributes;
	}

	private void openConnection() throws SQLException, ClassNotFoundException {
		if (connection != null && !connection.isClosed()) {
			return;
		}
		synchronized (LoaderClass.plugin) {
			if (isConnected())
				return;
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + at,
						username, password);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
