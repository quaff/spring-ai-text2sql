package com.cywb.text2sql;

public interface DatabaseInformation {

	String getDatabaseProductName();

	String getDatabaseVersion();

	String getTableSchemas();

}
