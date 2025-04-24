package com.demo.ai.text2sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSourceDatabaseInformation implements DatabaseInformation {

    private final String databaseProductName;

    private final String tableSchemas;

    public DataSourceDatabaseInformation(DataSource dataSource) throws Exception {
        try (Connection con = dataSource.getConnection()) {
            DatabaseMetaData dmd = con.getMetaData();
            this.databaseProductName = dmd.getDatabaseProductName();
            List<Table> tables = new ArrayList<>();
            try (ResultSet rs = dmd.getTables(con.getCatalog(), null, null,
                    new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
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
            this.tableSchemas = new ObjectMapper().writeValueAsString(tables);
        }
    }

    @Override
    public String getDatabaseProductName() {
        return this.databaseProductName;
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
