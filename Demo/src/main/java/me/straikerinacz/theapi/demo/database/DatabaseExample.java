package me.straikerinacz.theapi.demo.database;

import java.sql.SQLException;

import me.devtec.shared.database.DatabaseAPI;
import me.devtec.shared.database.DatabaseAPI.DatabaseType;
import me.devtec.shared.database.DatabaseAPI.SqlDatabaseSettings;
import me.devtec.shared.database.DatabaseHandler;
import me.devtec.shared.database.DatabaseHandler.InsertQuery;
import me.devtec.shared.database.DatabaseHandler.Row;
import me.devtec.shared.database.DatabaseHandler.SelectQuery;
import me.devtec.shared.database.DatabaseHandler.UpdateQuery;
import me.devtec.shared.utility.StringUtils;

public class DatabaseExample {
	private static DatabaseHandler database;
	public static void init() throws SQLException {
		DatabaseExample.database=DatabaseExample.openConnection(DatabaseType.MARIADB, "localhost", 3306, "theapi_testing_database", "theapi_testing_username", "theapi_testing_password");

		// Create table if not exists.
		//     Row(fieldName, fieldType, nullable)
		DatabaseExample.database.createTable("testing_table", new Row[] { new Row("user", "varchar(16)", false), new Row("knowledge", "int(255)", false) });

		//Select query
		SelectQuery query = SelectQuery.table("testing_table", "knowledge").where("user", "StraikerinaCZ").limit(1); //select knowledge
		if(!DatabaseExample.database.exists(query))
			/**
			 * testing_table | user varchar(16) | knowledge int(255)
			 */
			DatabaseExample.database.insert(InsertQuery.table("testing_table", "StraikerinaCZ", "0"));
		//Result#getValue returns String[<selected values> - * for all]
		String knowledge = DatabaseExample.database.get(query).getValue()[0];
		System.out.println("StraikerinaCZ's knowledge is "+knowledge);
		//add +1 to knowledge
		DatabaseExample.database.update(UpdateQuery.table("testing_table").where("user", "StraikerinaCZ").value("knowledge", StringUtils.getInt(knowledge)+1+"").limit(1));


		//Example with "*" or empty selections

		//Select query
		//just leave after table values empty, bcs we want to select all, or select "*" - it's same
		query = SelectQuery.table("testing_table").where("user", "StraikerinaCZ").limit(1); //select knowledge
		if(!DatabaseExample.database.exists(query))
			/**
			 * testing_table | user varchar(16) | knowledge int(255)
			 */
			DatabaseExample.database.insert(InsertQuery.table("testing_table", "StraikerinaCZ", "0"));
		//Result#getValue returns String[<selected values> - * for all]
		//We select second value, bcs first is "user" and second is "knowledge" - viz. table structure
		knowledge = DatabaseExample.database.get(query).getValue()[1];
		System.out.println("StraikerinaCZ's knowledge is "+knowledge);
		//add +1 to knowledge
		DatabaseExample.database.update(UpdateQuery.table("testing_table").where("user", "StraikerinaCZ").value("knowledge", StringUtils.getInt(knowledge)+1+"").limit(1));
	}

	public static DatabaseHandler openConnection(DatabaseType type, String ip, int port, String database, String username, String password) throws SQLException {
		return DatabaseAPI.openConnection(type, new SqlDatabaseSettings(type, ip, port, database, username, password));
	}
}
