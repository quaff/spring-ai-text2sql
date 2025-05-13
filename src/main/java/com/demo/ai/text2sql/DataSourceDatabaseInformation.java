package com.demo.ai.text2sql;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableConfigurationProperties(DatabaseInformationProperties.class)
public class DataSourceDatabaseInformation implements DatabaseInformation {

    private final DataSource dataSource;

    private final DatabaseInformationProperties databaseInformationProperties;

    private final String databaseProductName;

    private final String databaseVersion;

    public DataSourceDatabaseInformation(DataSource dataSource, DatabaseInformationProperties databaseInformationProperties) throws Exception {
        this.dataSource = dataSource;
        this.databaseInformationProperties = databaseInformationProperties;
        if (databaseInformationProperties.getDatabaseProductName() != null) {
            this.databaseProductName = databaseInformationProperties.getDatabaseProductName();
            this.databaseVersion = databaseInformationProperties.getDatabaseVersion();
        } else {
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
        if (databaseInformationProperties.getTableSchemas() != null) {
            tableSchemas = databaseInformationProperties.getTableSchemas();
        } else {
            try (Connection con = dataSource.getConnection()) {
                DatabaseMetaData dmd = con.getMetaData();
                List<Table> tables = new ArrayList<>();
                try (ResultSet rs = dmd.getTables(con.getCatalog(), con.getSchema(), null,
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
                tableSchemas = tables.toString();
            } catch (SQLException ex) {
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
