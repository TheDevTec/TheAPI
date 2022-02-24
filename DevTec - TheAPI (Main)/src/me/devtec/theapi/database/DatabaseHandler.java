package me.devtec.theapi.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import me.devtec.shared.Pair;
import me.devtec.theapi.database.DatabaseAPI.DatabaseType;

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
		protected List<Pair> values = new ArrayList<>();
		
		private SelectQuery(String table, String... value) {
			this.table = table;
			search = value==null || value.length==0 ? new String[]{"*"} : value;
		}
		
		public static SelectQuery table(@Nonnull String table, @Nullable String... search) {
			return new SelectQuery(table, search);
		}
		
		public SelectQuery where(@Nonnull String key, @Nonnull String value) {
			values.add(new Pair(Action.WHERE, new String[] {key,value}));
			return this;
		}
		
		public SelectQuery sort(@Nonnull String key, @Nonnull Sorting type) {
			values.add(new Pair(Action.SORT, new String[] {key,type.name()}));
			return this;
		}
		
		public SelectQuery limit(int limit) {
			this.limit=limit==0?null:""+limit;
			return this;
		}
		
		public SelectQuery limit(int limitFrom, int limitTo) {
			this.limit=limitFrom+","+limitTo;
			return this;
		}
		
		public String getTable() {
			return table;
		}
		
		public String[] getSearch() {
			return search;
		}
	}
	
	public static class InsertQuery {
		
		protected String table;
		protected List<String> values = new ArrayList<>();
		
		private InsertQuery(String table) {
			this.table = table;
		}
		
		public static InsertQuery table(@Nonnull String table, @Nonnull String... values) {
			InsertQuery query = new InsertQuery(table);
			for(String value : values)
				query.values.add(value);
			return query;
		}
		
		public String getTable() {
			return table;
		}
	}
	
	public static class UpdateQuery {
		
		protected String table;
		protected List<Pair> values = new ArrayList<>();
		protected String limit = "1";
		
		protected enum Action {
			WHERE, VALUE
		}
		
		private UpdateQuery(String table) {
			this.table = table;
		}
		
		public static UpdateQuery table(@Nonnull String table) {
			return new UpdateQuery(table);
		}
		
		public UpdateQuery where(@Nonnull String key, @Nonnull String value) {
			values.add(new Pair(Action.WHERE, new String[] {key, value}));
			return this;
		}
		
		public UpdateQuery value(@Nonnull String key, @Nonnull String value) {
			values.add(new Pair(Action.VALUE, new String[] {key, value}));
			return this;
		}
		
		public UpdateQuery limit(int limit) {
			this.limit=limit==0?null:""+limit;
			return this;
		}
		
		public UpdateQuery limit(int limitFrom, int limitTo) {
			this.limit=limitFrom+","+limitTo;
			return this;
		}
		
		public String getTable() {
			return table;
		}
	}
	
	public static class RemoveQuery {
		
		protected String table;
		protected List<String[]> values = new ArrayList<>();
		protected String limit = "1";
		
		private RemoveQuery(String table) {
			this.table = table;
		}
		
		public static RemoveQuery table(@Nonnull String table) {
			return new RemoveQuery(table);
		}
		
		public RemoveQuery where(@Nonnull String key, @Nonnull String value) {
			values.add(new String[] {key, value});
			return this;
		}
		
		public RemoveQuery limit(int limit) {
			this.limit=limit==0?null:""+limit;
			return this;
		}
		
		public RemoveQuery limit(int limitFrom, int limitTo) {
			this.limit=limitFrom+","+limitTo;
			return this;
		}
		
		public String getTable() {
			return table;
		}
	}

	public static class Row {
		private String field, type;
		private boolean nulled;
		private String key, defaultVal, extra;
		
		public Row(String fieldName, String fieldType, boolean nulled, String key, String defVal, String extra) {
			this.field=fieldName;
			this.type=fieldType;
			this.nulled=nulled;
			this.key=key;
			this.defaultVal=defVal;
			this.extra=extra;
		}
		
		public String getFieldName() {
			return field;
		}
		
		public String getFieldType() {
			return type;
		}
		
		public boolean isNulled() {
			return nulled;
		}
		
		public String getKey() {
			return key;
		}
		
		public String getDefaultValue() {
			return defaultVal;
		}
		
		public String getExtra() {
			return extra;
		}
	}
	
	public static class Result implements Iterator<Result> {
		private Result next;
		private String[] values;
		
		protected Result(String[] value) {
			this.values=value;
		}
		
		protected void nextResult(Result next) {
			if(next!=null)this.next=next;
		}
		
		@Nullable
		public Result next() {
			return next;
		}
		
		public boolean hasNext() {
			return next!=null;
		}
		
		public String[] getValue() {
			return values;
		}
	}
	
	public DatabaseType getType();
	
	public boolean isConnected() throws SQLException;
	
	public void open() throws SQLException;
	
	public void close() throws SQLException;
	
	public boolean exists(SelectQuery query) throws SQLException;
	
	public boolean createTable(String name, Row[] values) throws SQLException;
	
	public boolean deleteTable(String name) throws SQLException;
	
	@Nullable
	public Result get(SelectQuery query) throws SQLException;
	
	public boolean insert(InsertQuery query) throws SQLException;
	
	public boolean update(UpdateQuery query) throws SQLException;
	
	public boolean remove(RemoveQuery query) throws SQLException;
	
	public List<String> getTables() throws SQLException;
	
	public Row[] getTableValues(String name) throws SQLException;
}
