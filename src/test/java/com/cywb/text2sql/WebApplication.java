package com.cywb.text2sql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	// @org.springframework.context.annotation.Bean
	Text2Sql testText2Sql() {
		return new Text2Sql() {
			@Override
			public String query(String query) {
				try {
					Thread.sleep(1000); // simulate long running query
				}
				catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
				return "这是模拟的文本结果";
			}

			@Override
			public <T> T query(String query, Class<T> type) {
				return null;
			}

			@Override
			public String echarts(String query) {
				try {
					Thread.sleep(1000); // simulate long running query
				}
				catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
				if (query.contains("文本结果")) {
					// simulate error response
					return "这是模拟的文本结果";
				}
				return """
						{
						  "series": [
						    {
						      "name": "平均分",
						      "type": "bar",
						      "data": [
						        { "value": 84.33, "name": "一年级一班" },
						        { "value": 87.0, "name": "一年级二班" },
						        { "value": 70.67, "name": "二年级一班" },
						        { "value": 71.67, "name": "二年级二班" }
						      ]
						    }
						  ],
						  "xAxis": {
						    "type": "category",
						    "data": ["一年级一班", "一年级二班", "二年级一班", "二年级二班"]
						  },
						  "yAxis": {
						    "type": "value",
						    "name": "分数"
						  }
						}""";
			}
		};
	}

}
