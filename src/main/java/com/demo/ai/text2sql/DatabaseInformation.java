package com.demo.ai.text2sql;

public interface DatabaseInformation {

    String getDatabaseProductName();

    String getDatabaseVersion();

    String getTableSchemas();

}
