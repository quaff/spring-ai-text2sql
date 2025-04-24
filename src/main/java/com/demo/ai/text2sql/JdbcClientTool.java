package com.demo.ai.text2sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component
public class JdbcClientTool {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcClient jdbcClient;

    public JdbcClientTool(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Tool(description = "执行查询语句返回CSV格式结果")
    public String query(@ToolParam(description = "查询语句") String sql) {
        logger.info("Executing:\n{}", sql);
        List<Map<String, Object>> rows = jdbcClient.sql(sql)
                .query().listOfRows();
        if (rows.isEmpty())
            return "";
        List<String> list = new ArrayList<>(rows.size() + 1);
        list.add(String.join(",", rows.getFirst().keySet()));
        for (Map<String, Object> row : rows) {
            StringJoiner sj = new StringJoiner(",");
            for (Object value : row.values()) {
                sj.add(String.valueOf(value));
            }
            list.add(sj.toString());
        }
        String result = String.join("\n", list);
        logger.info("Executed result:\n{}", result);
        return result;
    }
}
