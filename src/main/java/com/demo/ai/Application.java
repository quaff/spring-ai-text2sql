package com.demo.ai;

import com.demo.ai.text2sql.Text2Sql;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		Text2Sql client = new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args).getBean(Text2Sql.class);
		System.out.println(client.query("谁考的分最高？谁考的分最少？平均分又是多少？"));
	}

}
