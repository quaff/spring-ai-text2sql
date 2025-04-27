package com.demo.ai.text2sql;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("database-information")
public class DatabaseInformationProperties {

    private String databaseProductName;

    private String databaseVersion;

    private String tableSchemas;

    private List<String> tablePatterns;

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public void setDatabaseProductName(String databaseProductName) {
        this.databaseProductName = databaseProductName;
    }

    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(String databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public String getTableSchemas() {
        return tableSchemas;
    }

    public void setTableSchemas(String tableSchemas) {
        this.tableSchemas = tableSchemas;
    }

    public List<String> getTablePatterns() {
        return tablePatterns;
    }

    public void setTablePatterns(List<String> tablePatterns) {
        this.tablePatterns = tablePatterns;
    }
}
