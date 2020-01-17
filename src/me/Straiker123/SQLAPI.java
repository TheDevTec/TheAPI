package me.Straiker123;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author Special thank to Firestone82
 *
 */
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
	            e.printStackTrace();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	 
		public boolean isClosed() {
			boolean i = true;
			if(statement!=null) {
			try {
				i=statement.isClosed();
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
				e.printStackTrace();
			}
			connection=null;
		    }
		}
		/**
		 * 
		 * @param column ColumnName
		 * @param table Table
		 * @param index SearchIndex
		 * @param value SearchValue
		 * @return String
		 */
		public List<Object> getValues(String column, String table, String index, String value) {
			List<Object> s = new ArrayList<Object>();
			try {
				ResultSet result = statement.executeQuery("SELECT "+column+" FROM "+table+" WHERE "+index+" = "+value+";");
				int i = 0;
				while(result.next()) {
				s.add(result.getObject(i));
				++i;
				}
			} catch (SQLException e) {
			}
			return s;
		}

		/**
		 * 
		 * @param column ColumnName
		 * @param table Table
		 * @param index Index To Remove
		 */
		public void removeIndex(String column, String table, String index) {
			try {
			statement.executeQuery("DELETE FROM "+table+" WHERE "+column+" = "+index+";");
			} catch (SQLException e) {
			}
		}

		/**
		 * 
		 * @param column ColumnName
		 * @param table Table
		 * @param index Index To Remove
		 */
		public void removeColumn(String column, String table) {
			try {
			statement.executeQuery("UPDATE "+table+" SET "+column+" = \"\";");
			} catch (SQLException e) {
			}
		}

		/**
		 * 
		 * @param table Table
		 * @return List<String>
		 */
		public List<String> getTabs(String table) {
			List<String> add = new ArrayList<String>();
		try {
			ResultSet result = statement.executeQuery("DESCRIBE "+table+";");
			int i = 0;
			while(result.next()) {
				add.add(result.getString(i));
			++i;
			}
		} catch (SQLException e) {
		}
		return add;
	}
		/**
		 * 
		 * @param column Column
		 * @param table Table
		 * @return List<String>
		 */
		public List<String> getKeys(String column, String table) {
			List<String> add = new ArrayList<String>();
		try {
			ResultSet result = statement.executeQuery("SELECT "+column+" FROM "+table+";");
			int i = 0;
			while(result.next()) {
				add.add(result.getString(i));
			++i;
			}
		} catch (SQLException e) {
		}
		return add;
	}
		/**
		 * 
		 * @param column Column Name
		 * @param table Table
		 * @param index Search Index
		 * @param value Search Value
		 * @return int
		 */
		public List<String> getStrings(String column, String table, String index, String value) {
			List<String> list = new ArrayList<String>();
			for(Object a : getValues(column, table, index, value)) {
				list.add(a.toString());
			}
			return list;
		}
		/**
		 * 
		 * @param column Column Name
		 * @param table Table
		 * @param index Search Index
		 * @param value Search Value
		 * @return int
		 */
		public List<Integer> getInts(String column, String table, String index, String value) {
			List<Integer> list = new ArrayList<Integer>();
			for(Object a : getValues(column, table, index, value)) {
				list.add(TheAPI.getNumbersAPI(a.toString()).getInt());
			}
			return list;
		}
		/**
		 * 
		 * @param column Column Name
		 * @param table Table
		 * @param index Search Index
		 * @param value Search Value
		 * @return double
		 */
		public List<Double> getDoubles(String column, String table, String index, String value) {
			List<Double> list = new ArrayList<Double>();
			for(Object a : getValues(column, table, index, value)) {
				list.add(TheAPI.getNumbersAPI(a.toString()).getDouble());
			}
			return list;
		}
		/**
		 * 
		 * @param column Column Name
		 * @param table Table
		 * @param index Search Index
		 * @param value Search Value
		 * @return long
		 */
		public List<Long> getLongs(String column, String table, String index, String value) {
			List<Long> list = new ArrayList<Long>();
			for(Object a : getValues(column, table, index, value)) {
				list.add(TheAPI.getNumbersAPI(a.toString()).getLong());
			}
			return list;
		}

		/**
		 * 
		 * @param column Column Name
		 * @param table Table
		 * @param index Search Index
		 * @param value Search Value
		 * @param set Value To Set
		 */
		public void add(String column, String table, String index, String value, Object set) {
			try {
				statement.executeQuery("UPDATE "+table+" SET "+column+" = "+set+" WHERE "+value+" = "+value+";");
			} catch (SQLException e) {
			}
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
