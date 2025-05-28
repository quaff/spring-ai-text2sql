package com.cywb.text2sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		Text2Sql text2Sql = new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE)
			.run(args)
			.getBean(Text2Sql.class);

		echarts(text2Sql);
	}

	public static void echarts(Text2Sql text2Sql) {
		String question = "每个年级的平均分是多少？";
		logger.info("用户提问：\n{}", question);
		String answer = text2Sql.echarts(question);
		logger.info("AI回答：\n{}", answer);
	}

	public static void multipleTable(Text2Sql text2Sql) {
		String question = "哪一个班的平均分最高？每个年级的平均分是多少？";
		logger.info("用户提问：\n{}", question);
		String answer = text2Sql.query(question);
		logger.info("AI回答：\n{}", answer);
	}

	public static void natural(Text2Sql text2Sql) {
		String question = "谁考的分最高？谁考的分最少？平均分又是多少？最接近平均分的是谁？";
		logger.info("用户提问：\n{}", question);
		String answer = text2Sql.query(question);
		logger.info("AI回答：\n{}", answer);
	}

	public static void structured(Text2Sql text2Sql) {
		String question = "最接近平均分的是谁？";
		logger.info("用户提问：\n{}", question);
		record Result(String name, int score, double averageScore) {
		}
		Result result = text2Sql.query(question, Result.class);
		logger.info("AI返回结构化结果：\n{}", result);
	}

	public static void simpleBoolean(Text2Sql text2Sql) {
		String question = "张三考的分是不是最高？";
		logger.info("用户提问：\n{}", question);
		Boolean result = text2Sql.query(question, Boolean.class);
		logger.info("AI返回结构化结果：\n{}", result);
	}

}
