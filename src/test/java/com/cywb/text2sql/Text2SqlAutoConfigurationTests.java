package com.cywb.text2sql;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcClientAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

	@Test
	void qualifiedDataSourceBeanShouldBeUsed() {
		contextRunner.withUserConfiguration(QualifiedDataSourceConfiguration.class).run(context -> {
			assertThat(context).hasBean("dataSource");
			assertThat(context).hasBean("text2SqlDataSource");

			assertThat(context.getBean(DataSourceDatabaseInformation.class).getTableSchemas())
				.isEqualTo("[{\"name\":\"TEST_TABLE\",\"columns\":[{\"name\":\"ID\",\"dataType\":\"INTEGER\"}]}]");
			assertThat(context.getBean(JdbcClientTool.class).query("SELECT * FROM test_table")).isEqualTo("ID\n1");
		});
	}

	@Configuration
	static class QualifiedDataSourceConfiguration {

		@Text2SqlDataSource
		@Bean(defaultCandidate = false)
		DataSource text2SqlDataSource() {
			DataSource dataSource = DataSourceBuilder.create()
				.url(EmbeddedDatabaseConnection.H2.getUrl("testdb"))
				.build();
			try (Connection conn = dataSource.getConnection()) {
				try (Statement stmt = conn.createStatement()) {
					stmt.execute("CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY)");
					stmt.execute("INSERT INTO test_table (id) VALUES (1)");
				}
			}
			catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			return dataSource;
		}

	}

}
