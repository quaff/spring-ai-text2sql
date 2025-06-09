package com.cywb.text2sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.sql.DataSource;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(Text2SqlProperties.class)
class Text2SqlAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	DataSourceDatabaseInformation dataSourceDatabaseInformation(
			@Text2SqlDataSource ObjectProvider<DataSource> text2SqlDataSource, DataSource dataSource,
			Text2SqlProperties text2SqlProperties) throws Exception {
		return new DataSourceDatabaseInformation(text2SqlDataSource.getIfAvailable(() -> dataSource),
				text2SqlProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	DatabaseInformationAdvisor databaseInformationAdvisor(DatabaseInformation databaseInformation) {
		return new DatabaseInformationAdvisor(databaseInformation);
	}

	@ToolBean
	@ConditionalOnMissingBean
	JdbcClientTool jdbcClientTool(@Text2SqlDataSource ObjectProvider<DataSource> text2SqlDataSource,
			DataSource dataSource, Text2SqlProperties text2SqlProperties) {
		return new JdbcClientTool(text2SqlDataSource.getIfAvailable(() -> dataSource), text2SqlProperties);
	}

	@ToolBean
	@ConditionalOnMissingBean
	TableSchemaTool tableSchemaTool(DatabaseInformation databaseInformation) {
		return new TableSchemaTool(databaseInformation);
	}

	@Bean
	@ConditionalOnMissingBean
	Text2Sql text2Sql(ChatClient.Builder builder, DatabaseInformationAdvisor databaseMetadataAdvisor,
			@ToolBean Object[] tools) {
		return new DefaultText2Sql(builder, databaseMetadataAdvisor, tools);
	}

	@ConditionalOnProperty(prefix = Text2SqlProperties.CONFIG_PREFIX, name = "logging", havingValue = "true")
	@Bean
	RestClientCustomizer loggingRestClientCustomizer() {
		return builder -> builder.requestInterceptor(new LoggingClientHttpRequestInterceptor());
	}

	@Bean
	Text2SqlController text2SqlController(Text2Sql text2Sql, Text2SqlProperties text2SqlProperties) {
		return new Text2SqlController(text2Sql, text2SqlProperties);
	}

	@Qualifier
	@Bean
	@Target({ ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	@interface ToolBean {

	}

}
