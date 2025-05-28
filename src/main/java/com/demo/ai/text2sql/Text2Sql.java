package com.demo.ai.text2sql;

public interface Text2Sql {

	String query(String query);

	<T> T query(String query, Class<T> type);

	String echarts(String query);

}
