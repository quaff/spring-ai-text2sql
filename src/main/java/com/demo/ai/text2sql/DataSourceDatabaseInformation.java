package com.demo.ai.text2sql;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableConfigurationProperties(DatabaseInformationProperties.class)
public class DataSourceDatabaseInformation implements DatabaseInformation {

    private final String databaseProductName;

    private final String databaseVersion;

    private final String tableSchemas;

    public DataSourceDatabaseInformation(DatabaseInformationProperties databaseInformationProperties, DataSource dataSource) throws Exception {
        try (Connection con = dataSource.getConnection()) {
            DatabaseMetaData dmd = con.getMetaData();
            if (databaseInformationProperties.getDatabaseProductName() != null) {
                this.databaseProductName = databaseInformationProperties.getDatabaseProductName();
            } else {
                this.databaseProductName = dmd.getDatabaseProductName();
            }
            if (databaseInformationProperties.getDatabaseVersion() != null) {
                this.databaseVersion = databaseInformationProperties.getDatabaseVersion();
            } else {
                this.databaseVersion = "%d.%d".formatted(dmd.getDatabaseMajorVersion(), dmd.getDatabaseMinorVersion());
            }
            if (databaseInformationProperties.getTableSchemas() != null) {
                this.tableSchemas = databaseInformationProperties.getTableSchemas();
            }
            else {
                List<Table> tables = new ArrayList<>();
                try (ResultSet rs = dmd.getTables(con.getCatalog(), null, null,
                        new String[]{"TABLE"})) {
                    while (rs.next()) {
                        String tableName = rs.getString("TABLE_NAME");
                        if (!isAllowed(databaseInformationProperties.getTablePatterns(), tableName)) {
                            continue;
                        }
                        List<Column> columns = new ArrayList<>();
                        try (ResultSet cols = dmd.getColumns(null, null, tableName, null)) {
                            while (cols.next()) {
                                columns.add(
                                        new Column(cols.getString("COLUMN_NAME"), cols.getString("TYPE_NAME"), cols.getString("REMARKS"))
                                );
                            }
                        }
                        tables.add(new Table(tableName, rs.getString("REMARKS"), rs.getString("TABLE_CAT"), rs.getString("TABLE_SCHEM"), columns));
                    }
                }
                this.tableSchemas = tables.toString();
            }
        }
    }

    private static boolean isAllowed(List<String> tablePatterns, String tableName) {
        if (CollectionUtils.isEmpty(tablePatterns)) {
            return true;
        }
        return PatternMatchUtils.simpleMatch(tablePatterns.toArray(new String[0]), tableName);
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
        return this.tableSchemas;
    }

    public record Table(
            String name,
            String description,
            String catalog,
            String schema,
            List<Column> columns) {
    }

    record Column(
            String name,
            String dataType,
            String description) {
    }
}
