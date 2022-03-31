package me.devtec.shared.database;

import java.io.File;
import java.sql.SQLException;

import me.devtec.shared.API;

public class DatabaseAPI {
	public static interface DatabaseSettings {
		public DatabaseSettings attributes(String attributes);
		
		public String getUser();
		
		public String getPassword();
		
		public String getConnectionString();
	}

	public static class SqlDatabaseSettings implements DatabaseSettings {
		private String ip;
		private String database;
		private String username;
		private String password;
		private int port;
		private String attributes;
		private String sqlType;
		
		public SqlDatabaseSettings(DatabaseType sqlType, String ip, int port, String database, String username, String password) {
			this.ip=ip;
			this.port=port;
			this.sqlType=sqlType.getName();
			this.database=database;
			this.username=username;
			this.password=password;
		}
		
		public DatabaseSettings attributes(String attributes) {
			this.attributes=attributes;
			return this;
		}

		@Override
		public String getUser() {
			return username;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public String getConnectionString() {
			if(sqlType.equals("sqlserver"))
				return "jdbc:sqlserver://"+ip+":"+port+";user="+username+";password="+password+";databaseName="+database+";integratedSecurity=true;" + (attributes==null?"":attributes);
			return "jdbc:"+sqlType+"://" + ip + ":" + port + "/" + database + (attributes==null?"":attributes);
		}
	}
	
	public static class SqliteDatabaseSettings implements DatabaseSettings {
		private String file;
		private String username;
		private String password;
		private String attributes;
		
		public SqliteDatabaseSettings(String file, String username, String password) {
			this.file=file;
			this.username=username;
			this.password=password;
		}
		
		public SqliteDatabaseSettings attributes(String attributes) {
			this.attributes=attributes;
			return this;
		}

		@Override
		public String getUser() {
			return username;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public String getConnectionString() {
			return "jdbc:sqlite://" + file + (attributes==null?"":attributes);
		}
	}
	
	public enum DatabaseType {
			MYSQL("mysql"),
			MARIADB("mariadb"),
			SQLSERVER("sqlserver"),
			SQLITE("sqlite"),
			H2("h2");
		
			private String name;
			DatabaseType(String name){
				this.name=name;
			}

			String getName() {
				return name;
			}
	}

	public static DatabaseHandler openConnection(DatabaseType type, DatabaseSettings settings) throws SQLException {
		switch(type) {
		case H2:
			checkOrDownloadIfNeeded("h2");
			try {
				Class.forName("org.h2.Driver"); 
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case MARIADB:
			checkOrDownloadIfNeeded("mariadb");
			try {
				Class.forName("org.mariadb.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case MYSQL:
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				//Probably Velocity or custom server
				checkOrDownloadIfNeeded("mysql");
			}
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case SQLITE:
			checkOrDownloadIfNeeded("sqlite");
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case SQLSERVER:
			checkOrDownloadIfNeeded("sqlserver");
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
			default:
				break;
		}
		return new SqlHandler(settings.getConnectionString(), settings);
	}

	private static void checkOrDownloadIfNeeded(String string) {
		File file = new File("plugins/TheAPI/libraries/"+string+".jar");
		if(!file.exists())
			API.library.downloadFileFromUrl("https://github.com/TheDevTec/TheAPI/raw/master/"+string+".jar", file);
		API.library.load(file);
	}
}
