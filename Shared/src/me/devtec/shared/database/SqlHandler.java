package me.devtec.shared.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.devtec.shared.database.DatabaseAPI.DatabaseSettings;
import me.devtec.shared.database.DatabaseAPI.DatabaseType;
import me.devtec.shared.database.DatabaseHandler.SelectQuery.Sorting;
import me.devtec.shared.scheduler.Tasker;
import me.devtec.shared.utility.StringUtils;

public class SqlHandler implements DatabaseHandler {
	private Connection sql;
	private DatabaseSettings settings;
	private String path;

	public SqlHandler(String path, DatabaseSettings settings) throws SQLException {
		this.settings = settings;
		this.path = path;
		open();
		new Tasker() {

			@Override
			public void run() {
				try {
					if (SqlHandler.this.isConnected())
						sql.prepareStatement("select 1").executeQuery().next();
				} catch (Exception doNotIddle) {
					try {
						if (SqlHandler.this.isConnected())
							sql.prepareStatement("select 1").executeQuery().next();
					} catch (Exception doNotIddle2) {
					}
				}
			}
		}.runRepeating(0, 20 * 60 * 3);
	}

	public String buildSelectCommand(SelectQuery query) {
		StringBuilder builder = new StringBuilder("select ");
		boolean first = true;
		for (String search : query.getSearch()) {
			if (!first)
				builder.append(',');
			else
				first = false;
			builder.append(search);
		}
		builder.append(' ');
		builder.append("from").append(' ');
		builder.append('`').append(query.table).append('`');
		first = true;
		for (String[] pair : query.where) {
			if (first) {
				first = false;
				builder.append(' ').append("where");
			} else
				builder.append("and");
			builder.append(' ').append('`').append(pair[0].replace("'", "\\'")).append('`').append('=').append('\'')
					.append(pair[1].replace("'", "\\'")).append('\'');
		}
		if (query.sorting != null)
			builder.append(' ').append("order").append(' ').append("by").append(' ').append('`')
					.append(StringUtils.join(query.sortingKey, ",").replace("'", "\\'")).append('`').append(' ')
					.append(query.sorting == Sorting.UP ? "DESC" : "ASC");
		if (query.limit != null)
			builder.append(' ').append("limit").append(' ').append(query.limit);
		return builder.toString();
	}

	public String buildInsertCommand(InsertQuery query) {
		StringBuilder builder = new StringBuilder("insert into ");
		builder.append('`').append(query.table).append('`').append(' ');
		builder.append("values").append('(');
		boolean first = true;
		for (String val : query.values) {
			if (first)
				first = false;
			else
				builder.append(',').append(' ');
			builder.append('"').append(val.replace("'", "\\'")).append('"');
		}
		return builder.append(')').toString();
	}

	public String buildUpdateCommand(UpdateQuery query) {
		StringBuilder builder = new StringBuilder("update ");
		builder.append('`').append(query.table).append('`').append(' ');
		builder.append("set");
		boolean first = true;
		for (String[] val : query.values) {
			if (first)
				first = false;
			else
				builder.append(',');
			builder.append(' ').append('`').append(val[0].replace("'", "\\'")).append('`').append('=').append('\'')
					.append(val[1].replace("'", "\\'")).append('\'');
		}
		if (!query.where.isEmpty()) {
			first = true;
			for (String[] val : query.where) {
				if (first) {
					first = false;
					builder.append(' ').append("where");
				} else
					builder.append(',');
				builder.append(' ').append('`').append(val[0].replace("'", "\\'")).append('`').append('=').append('\'')
						.append(val[1].replace("'", "\\'")).append('\'');
			}
		}
		if (query.limit != null)
			builder.append(' ').append("LIMIT").append(' ').append(query.limit);
		return builder.toString();
	}

	public String buildRemoveCommand(RemoveQuery query) {
		StringBuilder builder = new StringBuilder("delete from ");
		builder.append('`').append(query.table).append('`').append(' ');
		boolean first = true;
		for (String[] val : query.values) {
			if (!first)
				builder.append(' ').append("and").append(' ');
			else {
				first = false;
				builder.append(' ').append("where").append(' ');
			}
			builder.append('`').append(val[0].replace("'", "\\'")).append('`').append('=').append('\'')
					.append(val[1].replace("'", "\\'")).append('\'');
		}
		if (query.limit != null)
			builder.append(' ').append("LIMIT").append(' ').append(query.limit);
		return builder.toString();
	}

	@Override
	public boolean isConnected() throws SQLException {
		return sql != null && !sql.isClosed() && sql.isValid(0);
	}

	@Override
	public void open() throws SQLException {
		if (sql != null)
			try {
				sql.close();
			} catch (Exception er) {
			}
		sql = DriverManager.getConnection(path, settings.getUser(), settings.getPassword());
		sql.setAutoCommit(true);
	}

	@Override
	public void close() throws SQLException {
		sql.close();
		sql = null;
	}

	@Override
	public boolean exists(SelectQuery query) throws SQLException {
		ResultSet set = prepareStatement(buildSelectCommand(query)).executeQuery();
		if (set == null)
			return false;
		return set.next();
	}

	@Override
	public boolean createTable(String name, Row[] values) throws SQLException {
		return prepareStatement("CREATE TABLE IF NOT EXISTS `" + name + "`(" + buildTableValues(values) + ")")
				.execute();
	}

	public String buildTableValues(Row[] values) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Row row : values) {
			if (!first)
				builder.append(',');
			first = false;
			builder.append('`').append(row.getFieldName().replace("'", "\\'")).append('`').append(' ')
					.append(row.getFieldType().toLowerCase()).append(' ').append(row.isNulled() ? "NULL" : "NOT NULL");
		}
		return builder.toString();
	}

	@Override
	public boolean deleteTable(String name) throws SQLException {
		return prepareStatement("DROP TABLE " + name).execute();
	}

	@Override
	public Result get(SelectQuery query) throws SQLException {
		ResultSet set = prepareStatement(buildSelectCommand(query)).executeQuery();
		String[] lookup = query.getSearch();
		if (set != null && set.next()) {
			if (lookup.length == 1 && lookup[0].equals("*")) {
				int size = 1;
				List<String> val = new ArrayList<>();
				while (true)
					try {
						val.add(set.getObject(size++) + "");
					} catch (Exception err) {
						break;
					}
				Result res = new Result(val.toArray(new String[size -= 2]));
				Result main = res;
				Result next = main;
				while (set.next()) {
					String[] vals = new String[size];
					for (int i = 0; i < size; ++i)
						vals[i] = set.getObject(i + 1) + "";
					res = new Result(vals);
					next.nextResult(next = res);
				}
				return main;
			}
			String[] vals = new String[query.search.length];
			for (int i = 0; i < query.search.length; ++i)
				vals[i] = set.getObject(query.search[i]) + "";
			Result res = new Result(vals);
			Result main = res;
			Result next = main;
			while (set.next()) {
				vals = new String[query.search.length];
				for (int i = 0; i < query.search.length; ++i)
					vals[i] = set.getObject(query.search[i]) + "";
				res = new Result(vals);
				next.nextResult(next = res);
			}
			return main;
		}
		return null;
	}

	@Override
	public boolean insert(InsertQuery query) throws SQLException {
		return prepareStatement(buildInsertCommand(query)).executeUpdate() != 0;
	}

	@Override
	public boolean update(UpdateQuery query) throws SQLException {
		return prepareStatement(buildUpdateCommand(query)).executeUpdate() != 0;
	}

	private PreparedStatement prepareStatement(String sqlCommand) throws SQLException {
		try {
			if (!isConnected())
				open();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			return sql.prepareStatement(sqlCommand);
		} catch (SQLException err) {
			return sql.prepareStatement(sqlCommand); // one more time!
		}
	}

	@Override
	public boolean remove(RemoveQuery query) throws SQLException {
		return prepareStatement(buildRemoveCommand(query)).executeUpdate() != 0;
	}

	@Override
	public List<String> getTables() throws SQLException {
		ResultSet set = prepareStatement("SHOW TABLES").executeQuery();
		if (set != null && set.next()) {
			List<String> tables = new ArrayList<>();
			tables.add(set.getString(0));
			while (set.next())
				tables.add(set.getString(0));
			return tables;
		}
		return null;
	}

	@Override
	public Row[] getTableValues(String name) throws SQLException {
		ResultSet set = prepareStatement("DESCRIBE `" + name + "`").executeQuery();
		if (set == null || !set.next())
			return null;
		List<Row> rows = new ArrayList<>();
		while (set.next())
			rows.add(new Row(set.getString(0), set.getString(1), set.getString(2).equals("YES"), set.getString(3),
					set.getString(4), set.getString(5)));
		return rows.toArray(new Row[0]);
	}

	@Override
	public DatabaseType getType() {
		return DatabaseType.MYSQL;
	}

}
