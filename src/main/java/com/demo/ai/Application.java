package com.demo.ai;

import com.demo.ai.text2sql.Text2Sql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		Text2Sql text2Sql = new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args).getBean(Text2Sql.class);
		String question = "谁考的分最高？谁考的分最少？平均分又是多少？最接近平均分的是谁？";
		logger.info("用户提问：\n{}", question);
		String answer = text2Sql.query(question);
		logger.info("AI回答：\n{}", answer);
	}

}
