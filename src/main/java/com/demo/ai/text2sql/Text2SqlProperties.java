package com.demo.ai.text2sql;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(Text2SqlProperties.CONFIG_PREFIX)
public class Text2SqlProperties {

    public static final String CONFIG_PREFIX = "text2sql";

    private boolean logging;

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    private Database database = new Database();

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public static class Database {

        private String productName;

        private String version;

        private String tableSchemas;

        private List<String> tablePatterns;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
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
}
