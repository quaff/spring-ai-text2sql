package com.cywb.text2sql;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(Text2SqlProperties.CONFIG_PREFIX)
public class Text2SqlProperties {

	public static final String CONFIG_PREFIX = "text2sql";

	private boolean logging;

	private final Database database = new Database();

	private final Jdbc jdbc = new Jdbc();

	public boolean isLogging() {
		return this.logging;
	}

	public void setLogging(boolean logging) {
		this.logging = logging;
	}

	public Database getDatabase() {
		return this.database;
	}

	public Jdbc getJdbc() {
		return this.jdbc;
	}

	public static class Database {

		private String productName;

		private String version;

		private String tableSchemas;

		private List<String> tablePatterns;

		public String getProductName() {
			return this.productName;
		}

		public void setProductName(String productName) {
			this.productName = productName;
		}

		public String getVersion() {
			return this.version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getTableSchemas() {
			return this.tableSchemas;
		}

		public void setTableSchemas(String tableSchemas) {
			this.tableSchemas = tableSchemas;
		}

		public List<String> getTablePatterns() {
			return this.tablePatterns;
		}

		public void setTablePatterns(List<String> tablePatterns) {
			this.tablePatterns = tablePatterns;
		}

	}

	public static class Jdbc {

		private int maxRows = 500;

		public int getMaxRows() {
			return this.maxRows;
		}

		public void setMaxRows(int maxRows) {
			this.maxRows = maxRows;
		}

	}

}
