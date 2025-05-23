package com.demo.ai.text2sql;

import java.util.*;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.simple.JdbcClient;

public class JdbcClientTool {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcClient jdbcClient;

    public JdbcClientTool(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Tool(description = "执行查询语句返回CSV格式结果")
    public String query(@ToolParam(description = "查询语句") String sql) {
        logger.info("Executing:\n{}", sql);
        if (!isAllowed(sql)) {
            return "不能执行此查询语句";
        }
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

    private boolean isAllowed(String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            return statement instanceof Select;
        } catch (JSQLParserException e) {
            logger.error("Invalid SQL:\n" + sql, e);
            return false;
        }
    }

}
