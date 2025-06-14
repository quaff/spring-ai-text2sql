package com.cywb.text2sql;

import org.springframework.ai.tool.annotation.Tool;

public class TableSchemaTool {

	private final DatabaseInformation databaseInformation;

	public TableSchemaTool(DatabaseInformation databaseInformation) {
		this.databaseInformation = databaseInformation;
	}

	@Tool(description = "获取所有表结构元数据")
	public String getTableSchemas() {
		return this.databaseInformation.getTableSchemas();
	}

}
