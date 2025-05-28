package com.cywb.text2sql;

import org.junit.jupiter.api.Test;

import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcClientAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class Text2SqlAutoConfigurationTests {

	private static final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(Text2SqlAutoConfiguration.class, DataSourceAutoConfiguration.class,
				JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class,
				ChatClientAutoConfiguration.class, OpenAiChatAutoConfiguration.class))
		.withPropertyValues("spring.ai.openai.api-key=test");

	@Test
	void shouldCreateDefaultBeansWhenNoCustomBeansPresent() {
		contextRunner.run(context -> {
			assertThat(context).hasSingleBean(DataSourceDatabaseInformation.class);
			assertThat(context).hasSingleBean(DatabaseInformationAdvisor.class);
			assertThat(context).hasSingleBean(JdbcClientTool.class);
			assertThat(context).hasSingleBean(TableSchemaTool.class);
			assertThat(context).hasSingleBean(Text2Sql.class);
			assertThat(context).hasSingleBean(Text2SqlController.class);
		});
	}

	@Test
	void shouldCreateLoggingCustomizerWhenLoggingEnabled() {
		contextRunner.withPropertyValues("text2sql.logging=true").run(context -> {
			assertThat(context).hasBean("loggingRestClientCustomizer");
		});
	}

	@Test
	void shouldNotCreateLoggingCustomizerWhenLoggingDisabled() {
		contextRunner.withPropertyValues("text2sql.logging=false").run(context -> {
			assertThat(context).doesNotHaveBean("loggingRestClientCustomizer");
		});
	}

}
