package com.cywb.text2sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;

public class DataSourceDatabaseInformation implements DatabaseInformation {

	private static final ObjectMapper objectMapper = new ObjectMapper()
		.setSerializationInclusion(JsonInclude.Include.NON_NULL);

	private final DataSource dataSource;

	private final Text2SqlProperties.Database database;

	private final String databaseProductName;

	private final String databaseVersion;

	public DataSourceDatabaseInformation(DataSource dataSource, Text2SqlProperties text2SqlProperties)
			throws Exception {
		this.dataSource = dataSource;
		this.database = text2SqlProperties.getDatabase();
		if (this.database.getProductName() != null) {
			this.databaseProductName = this.database.getProductName();
			this.databaseVersion = this.database.getVersion();
		}
		else {
			try (Connection con = dataSource.getConnection()) {
				DatabaseMetaData dmd = con.getMetaData();
				this.databaseProductName = dmd.getDatabaseProductName();
				this.databaseVersion = "%d.%d".formatted(dmd.getDatabaseMajorVersion(), dmd.getDatabaseMinorVersion());
			}
		}
	}

	@Override
	public String getDatabaseProductName() {
		return this.databaseProductName;
	}

	@Override
	public String getDatabaseVersion() {
		return this.databaseVersion;
	}

	@Override
	public String getTableSchemas() {
		// get realtime table schemas
		String tableSchemas;
		if (this.database.getTableSchemas() != null) {
			tableSchemas = this.database.getTableSchemas();
		}
		else {
			try (Connection con = this.dataSource.getConnection()) {
				DatabaseMetaData dmd = con.getMetaData();
				List<Table> tables = new ArrayList<>();
				try (ResultSet rs = dmd.getTables(con.getCatalog(), con.getSchema(), null, new String[] { "TABLE" })) {
					while (rs.next()) {
						String tableName = rs.getString("TABLE_NAME");
						if (!isAllowed(this.database.getTablePatterns(), tableName)) {
							continue;
						}

						Map<String, String> foreignKeyMapping = new HashMap<>();
						try (ResultSet foreignKeys = dmd.getImportedKeys(null, null, tableName)) {
							while (foreignKeys.next()) {
								foreignKeyMapping.put(foreignKeys.getString("FKCOLUMN_NAME"), "%s(%s)".formatted(
										foreignKeys.getString("PKTABLE_NAME"), foreignKeys.getString("PKCOLUMN_NAME")));
							}
						}
						List<Column> columns = new ArrayList<>();
						try (ResultSet cols = dmd.getColumns(null, null, tableName, null)) {
							while (cols.next()) {
								String columnName = cols.getString("COLUMN_NAME");
								columns.add(new Column(columnName, cols.getString("TYPE_NAME"),
										cols.getString("REMARKS"), foreignKeyMapping.get(columnName)));
							}
						}
						tables.add(new Table(tableName, rs.getString("REMARKS"), columns));
					}
				}
				tableSchemas = objectMapper.writeValueAsString(tables);
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return tableSchemas;

	}

	private static boolean isAllowed(List<String> tablePatterns, String tableName) {
		if (CollectionUtils.isEmpty(tablePatterns)) {
			return true;
		}
		return PatternMatchUtils.simpleMatch(tablePatterns.toArray(new String[0]), tableName);
	}

	public record Table(String name, String description, List<Column> columns) {
	}

	record Column(String name, String dataType, String description, String foreignKeyReference) {
	}

}
