package com.demo.ai.text2sql;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.core.Ordered;

public class DatabaseInformationAdvisor implements BaseAdvisor {

    private static final String DEFAULT_SYSTEM_TEXT = """
            你是一个{databaseProductName}数据库专家。请帮助生成一个{databaseProductName} {databaseVersion}查询语句，然后执行这个查询语句回答问题。
            """;

    private final DatabaseInformation databaseInformation;

    public DatabaseInformationAdvisor(DatabaseInformation databaseInformation) {
        this.databaseInformation = databaseInformation;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        PromptTemplate template = new PromptTemplate(DEFAULT_SYSTEM_TEXT);
        template.add("databaseProductName", databaseInformation.getDatabaseProductName());
        template.add("databaseVersion", databaseInformation.getDatabaseVersion());
        return chatClientRequest.mutate()
                .prompt(chatClientRequest.prompt().augmentSystemMessage(template.render()))
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }
}