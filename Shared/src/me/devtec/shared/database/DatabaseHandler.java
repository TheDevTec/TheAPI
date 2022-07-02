package me.devtec.shared.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import me.devtec.shared.database.DatabaseAPI.DatabaseType;

public interface DatabaseHandler {
	public static class SelectQuery {
		protected enum Action {
			WHERE, SORT
		}

		public enum Sorting {
			UP, DOWN;
		}

		protected String table;
		protected String[] search;
		protected String limit;
		protected List<String[]> where = new ArrayList<>();

		protected Sorting sorting;
		protected List<String> sortingKey = new ArrayList<>();

		private SelectQuery(String table, String... value) {
			this.table = table;
			this.search = value == null || value.length == 0 ? new String[] { "*" } : value;
		}

		public static SelectQuery table(String table, String... search) {
			return new SelectQuery(table, search);
		}

		public SelectQuery where(String key, String value) {
			this.where.add(new String[] { key, value });
			return this;
		}

		public SelectQuery sortType(Sorting type) {
			this.sorting = type;
			return this;
		}

		public SelectQuery sortBy(String key) {
			this.sortingKey.add(key);
			return this;
		}

		public SelectQuery limit(int limit) {
			this.limit = limit == 0 ? null : "" + limit;
			return this;
		}

		public SelectQuery limit(int limitFrom, int limitTo) {
			this.limit = limitFrom + "," + limitTo;
			return this;
		}

		public String getTable() {
			return this.table;
		}

		public String[] getSearch() {
			return this.search;
		}
	}

	public static class InsertQuery {

		protected String table;
		protected List<String> values = new ArrayList<>();

		private InsertQuery(String table) {
			this.table = table;
		}

		public static InsertQuery table(String table, String... values) {
			InsertQuery query = new InsertQuery(table);
			Collections.addAll(query.values, values);
			return query;
		}

		public String getTable() {
			return this.table;
		}
	}

	public static class UpdateQuery {

		protected String table;
		protected List<String[]> where = new ArrayList<>();
		protected List<String[]> values = new ArrayList<>();
		protected String limit = "1";

		protected enum Action {
			WHERE, VALUE
		}

		private UpdateQuery(String table) {
			this.table = table;
		}

		public static UpdateQuery table(String table) {
			return new UpdateQuery(table);
		}

		public UpdateQuery where(String key, String value) {
			this.where.add(new String[] { key, value });
			return this;
		}

		public UpdateQuery value(String key, String value) {
			this.values.add(new String[] { key, value });
			return this;
		}

		public UpdateQuery limit(int limit) {
			this.limit = limit == 0 ? null : "" + limit;
			return this;
		}

		public UpdateQuery limit(int limitFrom, int limitTo) {
			this.limit = limitFrom + "," + limitTo;
			return this;
		}

		public String getTable() {
			return this.table;
		}
	}

	public static class RemoveQuery {

		protected String table;
		protected List<String[]> values = new ArrayList<>();
		protected String limit = "1";

		private RemoveQuery(String table) {
			this.table = table;
		}

		public static RemoveQuery table(String table) {
			return new RemoveQuery(table);
		}

		public RemoveQuery where(String key, String value) {
			this.values.add(new String[] { key, value });
			return this;
		}

		public RemoveQuery limit(int limit) {
			this.limit = limit == 0 ? null : "" + limit;
			return this;
		}

		public RemoveQuery limit(int limitFrom, int limitTo) {
			this.limit = limitFrom + "," + limitTo;
			return this;
		}

		public String getTable() {
			return this.table;
		}
	}

	public static class Row {
		private String field;
		private String type;
		private boolean nulled;
		private String key;
		private String defaultVal;
		private String extra;

		public Row(String fieldName, String fieldType, boolean nulled, String key, String defVal, String extra) {
			this.field = fieldName;
			this.type = fieldType;
			this.nulled = nulled;
			this.key = key;
			this.defaultVal = defVal;
			this.extra = extra;
		}

		public Row(String fieldName, String fieldType, boolean nulled) {
			this.field = fieldName;
			this.type = fieldType;
			this.nulled = nulled;
			this.key = "";
			this.defaultVal = "";
			this.extra = "";
		}

		public String getFieldName() {
			return this.field;
		}

		public String getFieldType() {
			return this.type;
		}

		public boolean isNulled() {
			return this.nulled;
		}

		public String getKey() {
			return this.key;
		}

		public String getDefaultValue() {
			return this.defaultVal;
		}

		public String getExtra() {
			return this.extra;
		}
	}

	public static class Result implements Iterator<Result> {
		private Result next;
		private String[] values;

		protected Result(String[] value) {
			this.values = value;
		}

		protected void nextResult(Result next) {
			if (next != null)
				this.next = next;
		}

		@Override
		public Result next() {
			return this.next;
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		public String[] getValue() {
			return this.values;
		}
	}

	public DatabaseType getType();

	public boolean isConnected() throws SQLException;

	public void open() throws SQLException;

	public void close() throws SQLException;

	public boolean exists(SelectQuery query) throws SQLException;

	public boolean createTable(String name, Row[] values) throws SQLException;

	public boolean deleteTable(String name) throws SQLException;

	public default Result select(SelectQuery query) throws SQLException {
		return this.get(query);
	}

	public Result get(SelectQuery query) throws SQLException;

	public boolean insert(InsertQuery query) throws SQLException;

	public default boolean set(UpdateQuery query) throws SQLException {
		return this.set(query);
	}

	public boolean update(UpdateQuery query) throws SQLException;

	public boolean remove(RemoveQuery query) throws SQLException;

	public List<String> getTables() throws SQLException;

	public Row[] getTableValues(String name) throws SQLException;
}
