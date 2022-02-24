package me.devtec.theapi.database;

import java.sql.SQLException;

import javax.annotation.Nullable;

import me.devtec.theapi.utils.theapiutils.LibraryLoader;

public class DatabaseAPI {
	public static interface DatabaseSettings {
		public DatabaseSettings attributes(String attributes);
		
		public String getUser();
		
		public String getPassword();
		
		public String getConnectionString();
	}

	public static class SqlDatabaseSettings implements DatabaseSettings {
		protected String ip, database, username, password;
		protected int port;
		protected String attributes, sqlType;
		
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
		protected String file, username, password;
		protected String attributes;
		
		public SqliteDatabaseSettings(String file, @Nullable String username, @Nullable String password) {
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
			LibraryLoader.load("h2");
			try {
				Class.forName ("org.h2.Driver"); 
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return new SqlHandler(settings.getConnectionString(), settings);
		case MARIADB:
			LibraryLoader.load("mariadb");
			try {
				Class.forName("org.mariadb.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return new SqlHandler(settings.getConnectionString(), settings);
		case MYSQL:
			return new SqlHandler(settings.getConnectionString(), settings);
		case SQLITE:
			LibraryLoader.load("sqlite");
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return new SqlHandler(settings.getConnectionString(), settings);
		case SQLSERVER:
			LibraryLoader.load("sqlserver");
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return new SqlHandler(settings.getConnectionString(), settings);
		}
		return null;
	}
}
