package com.demo.ai;

import com.demo.ai.text2sql.Text2Sql;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    // comment this annotation to use AI
    @Bean
    Text2Sql testText2Sql() {
        return new Text2Sql() {
            @Override
            public String query(String query) {
                try {
                    Thread.sleep(1000); // simulate long running query
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return "模拟结果";
            }

            @Override
            public <T> T query(String query, Class<T> type) {
                return null;
            }

            @Override
            public String echarts(String query) {
                try {
                    Thread.sleep(1000); // simulate long running query
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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
