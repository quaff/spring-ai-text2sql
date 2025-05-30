package com.cywb.text2sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;

public class JdbcClientTool {

	private static final Logger logger = LoggerFactory.getLogger(JdbcClientTool.class);

	private final JdbcClient jdbcClient;

	private final Text2SqlProperties text2SqlProperties;

	public JdbcClientTool(DataSource dataSource, Text2SqlProperties text2SqlProperties) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setMaxRows(text2SqlProperties.getJdbc().getMaxRows());
		this.jdbcClient = JdbcClient.create(jdbcTemplate);
		this.text2SqlProperties = text2SqlProperties;
	}

	@Tool(description = "执行查询语句返回CSV格式结果")
	public String query(@ToolParam(description = "查询语句") String sql) {
		sql = sql.trim();
		if (!isAllowed(sql)) {
			logger.info("Banned SQL:\n{}", sql);
			return "不能执行此查询语句";
		}
		logger.info("Executing:\n{}", sql);
		List<Map<String, Object>> rows = this.jdbcClient.sql(sql).query().listOfRows();
		if (rows.isEmpty()) {
			return "";
		}
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
		if (this.text2SqlProperties.isLogging()) {
			logger.info("Executed result:\n{}", result);
		}
		return result;
	}

	private boolean isAllowed(String sql) {
		String lowerSql = sql.toLowerCase(Locale.ROOT);
		return !lowerSql.startsWith("update") && !lowerSql.startsWith("delete") && !lowerSql.startsWith("insert")
				&& !lowerSql.startsWith("merge");
	}

}
